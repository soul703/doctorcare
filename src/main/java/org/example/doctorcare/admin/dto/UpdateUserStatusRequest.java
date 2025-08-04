package org.example.doctorcare.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotNull(message = "Trạng thái hoạt động không được để trống")
    private Boolean isActive;
}