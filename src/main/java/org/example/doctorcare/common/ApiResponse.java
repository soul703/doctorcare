package org.example.doctorcare.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
/**
 * Lớp đại diện cho phản hồi API chung.
 * Sử dụng để trả về các phản hồi từ các controller trong ứng dụng.
 *
 * @param <T> Kiểu dữ liệu của phần dữ liệu trả về (data).
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private int statusCode;
    private String message;
    private T data;
    private List<String> errors;


    private ApiResponse(HttpStatus status, String message, T data) {
        this.statusCode = status.value();
        this.message = message;
        this.data = data;
    }


    private ApiResponse(HttpStatus status, String message, List<String> errors) {
        this.statusCode = status.value();
        this.message = message;
        this.errors = errors;
    }

    // --- Các phương thức Factory tĩnh để tạo response dễ dàng ---

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message, T data) {
        return new ResponseEntity<>(new ApiResponse<>(status, message, data), status);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message) {
        return success(status, message, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, List<String> errors) {
        return new ResponseEntity<>(new ApiResponse<>(status, message, errors), status);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        return error(status, message, null);
    }
}
