package org.example.doctorcare.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.doctorcare.auth.dto.*;
import org.example.doctorcare.common.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ApiResponse.success(HttpStatus.OK, "Đăng nhập thành công", authResponse);
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()") // Best practice: Chỉ người đã đăng nhập mới có thể logout
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        authService.logout(authHeader);
        return ApiResponse.success(HttpStatus.OK, "Đăng xuất thành công.");
    }

    /**
     * Endpoint để làm mới access token.
     *
     * @param request Chứa refresh token.
     * @return Một cặp token mới (hoặc chỉ access token mới).
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request);
        return ApiResponse.success(HttpStatus.OK, "Làm mới token thành công", authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        // Trả về tin nhắn (message) tương ứng khi đăng ký thành công
        return ApiResponse.success(HttpStatus.CREATED, "Đăng ký thông tin thành công!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.handleForgotPassword(request);
        // Luôn trả về thông báo này để bảo mật
        String message = "Nếu email của bạn tồn tại trong hệ thống, chúng tôi đã gửi một hướng dẫn để đặt lại mật khẩu.";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success(HttpStatus.OK, "Mật khẩu của bạn đã được đặt lại thành công.");
    }
}
