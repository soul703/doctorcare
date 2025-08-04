package org.example.doctorcare.user;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.doctorcare.common.ApiResponse;
import org.example.doctorcare.public_api.booking.BookingService;
import org.example.doctorcare.public_api.booking.dto.CreateBookingRequest;
import org.example.doctorcare.security.CustomUserDetails;
import org.example.doctorcare.user.dto.UpdateProfileRequest;
import org.example.doctorcare.user.dto.UserProfileDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "User Management", description = "Các API dành cho người dùng đã đăng nhập")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getCurrentUserProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        UserProfileDTO userProfile = userService.getUserProfile(currentUser.getId());
        return ApiResponse.success(HttpStatus.OK, "Lấy thông tin cá nhân thành công", userProfile);
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateCurrentUserProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserProfileDTO updatedProfile = userService.updateUserProfile(currentUser.getId(), request);
        return ApiResponse.success(HttpStatus.OK, "Cập nhật thông tin thành công", updatedProfile);
    }
}


