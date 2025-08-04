package org.example.doctorcare.auth;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.doctorcare.auth.dto.*;
import org.example.doctorcare.exception.custom.BadRequestException;
import org.example.doctorcare.exception.custom.ConflictException;
import org.example.doctorcare.exception.custom.TokenRefreshException;
import org.example.doctorcare.mail.EmailService;
import org.example.doctorcare.security.JwtTokenProvider;
import org.example.doctorcare.security.TokenBlacklistService;
import org.example.doctorcare.user.Role;
import org.example.doctorcare.user.RoleRepository;
import org.example.doctorcare.user.User;
import org.example.doctorcare.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * Service xử lý các chức năng liên quan đến xác thực và đăng nhập hệ thống.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final int EXPIRATION_MINUTES = 15;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    /**
     * Xử lý đăng nhập người dùng.
     * @param loginRequest yêu cầu chứa email và mật khẩu
     * @return AuthResponse chứa access token và refresh token
     */
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Không thể tìm thấy user đã được xác thực"));

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        Token refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    /**
     * Xử lý đăng xuất, xóa refresh token và đưa access token vào blacklist.
     * @param authHeader chuỗi Authorization chứa Bearer token
     */
    @Transactional
    public void logout(String authHeader) {
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("Logout attempt with invalid or missing Authorization header.");
            return;
        }

        final String accessToken = authHeader.substring(7);

        try {
            String userEmail = jwtTokenProvider.getUsernameFromJWT(accessToken);
            if (userEmail == null) {
                log.warn("Logout attempt with an invalid token (email not found).");
                return;
            }

            userRepository.findByEmail(userEmail).ifPresent(user -> {
                refreshTokenService.deleteByUserId(user.getId());
                log.info("Refresh token for user {} has been deleted.", user.getEmail());
            });

            long remainingExpiration = jwtTokenProvider.getRemainingExpirationInMillis(accessToken);
            if (remainingExpiration > 0) {
                tokenBlacklistService.blacklistToken(accessToken, remainingExpiration);
                log.info("Access token for user {} has been blacklisted.", userEmail);
            }

        } catch (Exception e) {
            log.error("Error during logout process for token: {}", accessToken, e);
        }
    }

    /**
     * Làm mới access token bằng refresh token còn hiệu lực.
     * @param request RefreshTokenRequest
     * @return AuthResponse với access token mới
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(Token::getUser)
                .map(user -> {
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            "",
                            Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName()))
                    );

                    String newAccessToken = jwtTokenProvider.generateAccessTokenFromUserDetails(userDetails);
                    return new AuthResponse(newAccessToken, requestRefreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token không tồn tại trong CSDL!"));
    }

    /**
     * Đăng ký tài khoản mới.
     * @param request RegisterRequest chứa thông tin đăng ký
     */
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration attempt with existing email: {}", request.getEmail());
            throw new ConflictException("Địa chỉ email này đã được sử dụng. Vui lòng chọn email khác.");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy vai trò ROLE_USER."));

        User newUser = new User();
        newUser.setUsername(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setGender(request.getGender());
        newUser.setPhone(request.getPhone());
        newUser.setAddress(request.getAddress());
        newUser.setRole(userRole);
        newUser.setActive(true);

        userRepository.save(newUser);
        log.info("New user registered successfully with email: {}", newUser.getEmail());

        try {
            emailService.sendWelcomeEmail(newUser);
            log.info("Welcome email sent to {}", newUser.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", newUser.getEmail(), e.getMessage());
        }
    }

    /**
     * Gửi email reset mật khẩu cho người dùng nếu email tồn tại.
     * @param request yêu cầu chứa email
     */
    @Transactional
    public void handleForgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            log.warn("Forgot password attempt for non-existent email: {}", request.getEmail());
            return;
        }

        User user = userOptional.get();
        passwordResetTokenRepository.deleteByUser_Id(user.getId());

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(EXPIRATION_MINUTES * 60);
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);

        passwordResetTokenRepository.save(resetToken);
        log.info("Generated password reset token for user: {}, token: {}", user.getEmail(), token);

        try {
            String resetUrl = "http://localhost:3000/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetUrl);
            log.info("Password reset email sent to {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}", user.getEmail(), e);
        }
    }

    /**
     * Đặt lại mật khẩu dựa trên token hợp lệ.
     * @param request chứa token và mật khẩu mới
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Token reset mật khẩu không hợp lệ."));

        if (passwordResetToken.isExpired()) {
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new BadRequestException("Token reset mật khẩu đã hết hạn.");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);
        log.info("Password has been reset successfully for user: {}", user.getEmail());
    }
}
