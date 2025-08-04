package org.example.doctorcare.public_api.specialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) này được sử dụng để trả về thông tin tóm tắt
 * của các chuyên khoa nổi bật cho các API công khai.
 * Nó chỉ chứa các trường cần thiết cho việc hiển thị, giúp API response nhẹ và bảo mật.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopSpecializationDTO {

    /**
     * ID của chuyên khoa, dùng để điều hướng hoặc gọi API chi tiết.
     */
    private Long id;

    /**
     * Tên của chuyên khoa để hiển thị cho người dùng.
     * Ví dụ: "Cơ Xương Khớp"
     */
    private String name;

    /**
     * URL của hình ảnh đại diện cho chuyên khoa.
     */
    private String image;

    /**
     * Số lượt đặt lịch (booking) của chuyên khoa này.
     * Trường này được tính toán bởi câu lệnh query và có thể dùng để debug,
     * hoặc hiển thị cho người dùng nếu cần.
     *
     * @JsonInclude(JsonInclude.Include.NON_NULL) có thể được thay bằng @JsonIgnore
     * nếu bạn không muốn trường này xuất hiện trong JSON response cuối cùng.
     */
    private Long bookingCount;

}