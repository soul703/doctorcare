package org.example.doctorcare.public_api.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookingRequest {
    @NotNull(message = "ID của bác sĩ không được để trống")
    private Long doctorId;

    // Giả sử frontend sẽ gửi scheduleId để xác định chính xác ca khám
    @NotNull(message = "ID của lịch khám không được để trống")
    private Long scheduleId;

    // Các thông tin khác như lý do khám có thể thêm vào đây
    private String reason;
}
