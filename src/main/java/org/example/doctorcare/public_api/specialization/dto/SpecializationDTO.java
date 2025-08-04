package org.example.doctorcare.public_api.specialization.dto;

import lombok.Data;
import org.example.doctorcare.public_api.specialization.Specialization;

import java.time.LocalDateTime;

@Data
public class SpecializationDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SpecializationDTO fromEntity(Specialization entity) {
        SpecializationDTO dto = new SpecializationDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImage(entity.getImage());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
