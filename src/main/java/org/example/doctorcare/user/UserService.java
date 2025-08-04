package org.example.doctorcare.user;


import lombok.RequiredArgsConstructor;
import org.example.doctorcare.exception.custom.ResourceNotFoundException;
import org.example.doctorcare.user.dto.UpdateProfileRequest;
import org.example.doctorcare.user.dto.UserProfileDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        return UserProfileDTO.fromEntity(user);
    }

    @Transactional
    public UserProfileDTO updateUserProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        // Cập nhật các trường thông tin
        user.setUsername(request.getName());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setAvatar(request.getAvatar());

        User updatedUser = userRepository.save(user);
        return UserProfileDTO.fromEntity(updatedUser);
    }
}