package org.example.doctorcare.schedule.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateScheduleRequest {

    // Ví dụ: "2025-07-26"
    @NotNull(message = "Ngày làm việc không được để trống")
    @FutureOrPresent(message = "Ngày làm việc phải là ngày hiện tại hoặc trong tương lai")
    private LocalDate date;

    // Ví dụ: "T1" (ca sáng), "T2" (ca chiều)
    @NotBlank(message = "Khung giờ không được để trống")
    private String timeType;

    @NotNull(message = "Số lượng bệnh nhân tối đa không được để trống")
    @Min(value = 1, message = "Số lượng bệnh nhân tối đa phải lớn hơn 0")
    private Integer maxBooking;
}
