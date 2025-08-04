package org.example.doctorcare.admin.dto;

import lombok.Data;
import org.example.doctorcare.user.User;

import java.time.LocalDateTime;

@Data
public class UserManagementDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String roleName;
    private boolean isActive;
    private LocalDateTime createdAt;

    public static UserManagementDTO fromEntity(User user) {
        UserManagementDTO dto = new UserManagementDTO();
        dto.setId(user.getId());
        dto.setName(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getName());
        }
        return dto;
    }
}