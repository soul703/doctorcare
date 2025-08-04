package org.example.doctorcare.admin.dto;

import lombok.Data;
import org.example.doctorcare.user.User;

@Data
public class DoctorListItemDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private boolean isActive;

    public static DoctorListItemDTO fromEntity(User user) {
        DoctorListItemDTO dto = new DoctorListItemDTO();
        dto.setId(user.getId());
        dto.setName(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setActive(user.isActive());
        return dto;
    }
}