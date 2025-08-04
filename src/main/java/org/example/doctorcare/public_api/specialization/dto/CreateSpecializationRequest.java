package org.example.doctorcare.public_api.specialization.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSpecializationRequest {
    @NotBlank(message = "Tên chuyên khoa không được để trống")
    private String name;
    private String description;
    private String image;
}