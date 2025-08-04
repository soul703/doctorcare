package org.example.doctorcare.public_api.clinic.dto;

import lombok.Data;
import org.example.doctorcare.public_api.clinic.Clinic;

import java.time.LocalDateTime;

@Data
public class ClinicDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String image;
    private String description;
    private String introductionHTML;
    private String introductionMarkdown;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClinicDTO fromEntity(Clinic entity) {
        ClinicDTO dto = new ClinicDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        dto.setImage(entity.getImage());
        dto.setDescription(entity.getDescription());
        dto.setIntroductionHTML(entity.getIntroductionHTML());
        dto.setIntroductionMarkdown(entity.getIntroductionMarkdown());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}