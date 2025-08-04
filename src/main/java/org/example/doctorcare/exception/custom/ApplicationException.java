package org.example.doctorcare.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Lớp exception cơ sở cho toàn bộ ứng dụng.
 * Tất cả các exception nghiệp vụ tùy chỉnh nên kế thừa từ lớp này.
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ApplicationException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}