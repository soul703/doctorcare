package org.example.doctorcare.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String name;
    private String address;
    private String phone;
    private String gender;
    private String avatar; // URL đến avatar mới
}
