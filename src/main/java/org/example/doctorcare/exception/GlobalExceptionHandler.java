
package org.example.doctorcare.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.doctorcare.common.ApiResponse;
import org.example.doctorcare.exception.custom.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lớp xử lý lỗi toàn cục cho toàn bộ ứng dụng.
 * Bắt các exception được ném ra từ các tầng Controller và Service,
 * sau đó định dạng chúng thành một `ApiResponse` nhất quán.
 *
 * @ControllerAdvice cho phép lớp này hoạt động như một AOP interceptor cho các exception.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Xử lý các exception tùy chỉnh của ứng dụng (Kế thừa từ ApplicationException)
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleApplicationException(ApplicationException ex) {
        log.warn("ApplicationException: {}", ex.getMessage()); // Ghi log ở mức WARN cho lỗi nghiệp vụ
        return ApiResponse.error(ex.getStatus(), ex.getMessage());
    }

    // 2. Xử lý lỗi validation (@Valid) - HTTP 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMessages.add(String.format("%s: %s", fieldName, errorMessage));
        });
        log.warn("Validation error: {}", errorMessages);
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào không hợp lệ.", errorMessages);
    }

    // 3. Xử lý lỗi từ chối truy cập của Spring Security - HTTP 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("AccessDeniedException: {}", ex.getMessage());
        return ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào tài nguyên này.");
    }

    // 4. Xử lý lỗi sai phương thức HTTP (GET, POST,...) - HTTP 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("Phương thức %s không được hỗ trợ cho đường dẫn này. Các phương thức được hỗ trợ là %s.",
                ex.getMethod(),
                Objects.toString(ex.getSupportedHttpMethods()));
        log.warn(message);
        return ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    // 5. Xử lý lỗi sai kiểu dữ liệu của tham số (ví dụ: truyền chữ vào số) - HTTP 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Tham số '%s' có giá trị '%s' không hợp lệ. Kiểu dữ liệu mong muốn là '%s'.",
                ex.getName(),
                ex.getValue(),
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        log.warn(message);
        return ApiResponse.error(HttpStatus.BAD_REQUEST, message);
    }

    // 6. Xử lý lỗi khi request body bị thiếu hoặc không đọc được - HTTP 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Yêu cầu không hợp lệ: Request body bị thiếu hoặc sai định dạng JSON.";
        log.warn("{}: {}", message, ex.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, message);
    }

    // 7. Xử lý tất cả các lỗi còn lại (Catch-All) - HTTP 500
    // Đây là handler cuối cùng và quan trọng nhất
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUncaughtException(Exception ex) {
        // CRITICAL: Luôn ghi log đầy đủ stack trace của các lỗi không xác định
        log.error("Lỗi hệ thống không xác định đã xảy ra", ex);

        // KHÔNG BAO GIỜ trả về chi tiết của lỗi hệ thống cho client (ví dụ: NullPointerException)
        String message = "Đã có lỗi xảy ra ở hệ thống. Vui lòng liên hệ quản trị viên để được hỗ trợ.";
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}