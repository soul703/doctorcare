package org.example.doctorcare.user.dto;


import lombok.Data;
import org.example.doctorcare.user.User;

@Data
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String gender;
    private String avatar;

    // Factory method để chuyển đổi từ Entity sang DTO
    public static UserProfileDTO fromEntity(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setName(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());
        dto.setAvatar(user.getAvatar());
        return dto;
    }
}
