package org.example.doctorcare.exception.custom;

import org.springframework.http.HttpStatus;

/**
 * Được ném ra khi xảy ra lỗi trong quá trình xác thực.
 * Ví dụ: JWT token không hợp lệ, sai thông tin đăng nhập.
 * Ánh xạ tới mã trạng thái HTTP 401 Unauthorized.
 */
public class AuthenticationException extends ApplicationException {

    public AuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}