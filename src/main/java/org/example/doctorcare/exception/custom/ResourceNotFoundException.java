package org.example.doctorcare.exception.custom;

import org.springframework.http.HttpStatus;

/**
 * Được ném ra khi không tìm thấy một tài nguyên được yêu cầu.
 * Ánh xạ tới mã trạng thái HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}