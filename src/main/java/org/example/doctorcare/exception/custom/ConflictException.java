package org.example.doctorcare.exception.custom;

import org.springframework.http.HttpStatus;

/**
 * Được ném ra khi một yêu cầu không thể được hoàn thành do xung đột với trạng thái hiện tại của tài nguyên.
 * Ví dụ: Cố gắng tạo một người dùng với email đã tồn tại.
 * Ánh xạ tới mã trạng thái HTTP 409 Conflict.
 */
public class ConflictException extends ApplicationException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}