package org.example.doctorcare.exception.custom;

import org.springframework.http.HttpStatus;

/**
 * Được ném ra khi yêu cầu của người dùng chứa dữ liệu không hợp lệ về mặt logic nghiệp vụ.
 * Ví dụ: Mật khẩu cũ không chính xác, cố gắng thực hiện một hành động không được phép.
 * Ánh xạ tới mã trạng thái HTTP 400 Bad Request.
 */
public class BadRequestException extends ApplicationException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}