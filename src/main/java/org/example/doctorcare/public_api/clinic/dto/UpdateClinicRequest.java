package org.example.doctorcare.public_api.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateClinicRequest {
    @NotBlank(message = "Tên phòng khám không được để trống")
    private String name;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    private String phone;
    private String image;
    private String description;
    private String introductionHTML;
    private String introductionMarkdown;
}

