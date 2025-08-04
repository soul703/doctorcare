package org.example.doctorcare.admin.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDoctorInfoRequest {
    // Chỉ cho phép cập nhật thông tin chuyên môn
    @NotNull(message = "ID phòng khám không được để trống")
    private Integer clinicId;

    @NotNull(message = "ID chuyên khoa không được để trống")
    private Integer specializationId;
}