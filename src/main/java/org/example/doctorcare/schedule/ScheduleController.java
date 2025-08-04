package org.example.doctorcare.schedule;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.doctorcare.common.ApiResponse;
import org.example.doctorcare.schedule.dto.CreateScheduleRequest;
import org.example.doctorcare.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor/schedules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
@Tag(name = "Doctor Schedule Management", description = "Các API quản lý lịch làm việc của bác sĩ")
@SecurityRequirement(name = "bearerAuth") // Yêu cầu xác thực Bearer token
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createSchedule(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateScheduleRequest request) {

        scheduleService.createDoctorSchedule(currentUser.getId(), request);
        return ApiResponse.success(HttpStatus.CREATED, "Tạo lịch làm việc thành công.");
    }
}
