package org.example.doctorcare.auth;

import lombok.RequiredArgsConstructor;
import org.example.doctorcare.exception.custom.BadRequestException;
import org.example.doctorcare.exception.custom.ResourceNotFoundException;
import org.example.doctorcare.user.User;
import org.example.doctorcare.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private Long refreshTokenDurationMs;

    /**
     * Tìm một refresh token trong database.
     * @param token Chuỗi refresh token.
     * @return Optional chứa entity Token nếu tìm thấy.
     */
    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
    /**
     * Tạo một refresh token mới cho người dùng.
     * @param userId ID của người dùng.
     * @return Entity Token đã được lưu.
     */
    @Transactional
    public Token createRefreshToken(Long userId) {
        // Xóa token cũ nếu có để đảm bảo mỗi user chỉ có 1 refresh token tại 1 thời điểm
        tokenRepository.deleteByUserId(userId);

        Token refreshToken = new Token();
        refreshToken.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString()); // Tạo chuỗi ngẫu nhiên an toàn

        return tokenRepository.save(refreshToken);
    }

    /**
     * Kiểm tra xem refresh token có hết hạn hay không.
     * Nếu hết hạn, xóa nó khỏi DB và ném ra exception.
     * @param token Entity Token cần kiểm tra.
     * @return Entity Token nếu còn hạn.
     */
    public Token verifyExpiration(Token token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            tokenRepository.delete(token);
            throw new BadRequestException( "Refresh token đã hết hạn. Vui lòng đăng nhập lại!");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }

}