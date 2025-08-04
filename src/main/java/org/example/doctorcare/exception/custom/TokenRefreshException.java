package org.example.doctorcare.exception.custom;

import org.springframework.http.HttpStatus;

public class TokenRefreshException extends ApplicationException {
    public TokenRefreshException(String token, String message) {
        super(HttpStatus.FORBIDDEN, String.format("Thất bại cho token [%s]: %s", token, message));
    }
}