package org.example.doctorcare.doctor;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.doctorcare.common.ApiResponse;
import org.example.doctorcare.doctor.dto.BookingDetailForDoctorDTO;
import org.example.doctorcare.doctor.dto.CancelBookingRequest;
import org.example.doctorcare.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
@Tag(name = "Doctor Management", description = "Các API dành cho Bác sĩ")
@SecurityRequirement(name = "bearerAuth")
public class DoctorController {

    private final DoctorService doctorService;

    // 1. Xem danh sách lịch hẹn
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingDetailForDoctorDTO>>> getBookings(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {

        List<BookingDetailForDoctorDTO> bookings = doctorService.getBookingsForDoctor(currentUser.getId(), date, status);
        return ApiResponse.success(HttpStatus.OK, "Lấy danh sách lịch hẹn thành công.", bookings);
    }

    // 2. Xác nhận lịch hẹn
    @PatchMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<ApiResponse<Object>> confirmBooking(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long bookingId) {

        doctorService.confirmBooking(currentUser.getId(), bookingId);
        return ApiResponse.success(HttpStatus.OK, "Xác nhận lịch hẹn thành công.");
    }

    // 3. Hủy lịch hẹn
    @PatchMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<Object>> cancelBooking(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long bookingId,
            @Valid @RequestBody(required = false) CancelBookingRequest request) { // Lý do có thể không bắt buộc

        String reason = (request != null && request.getReason() != null) ? request.getReason() : "Lý do đột xuất";
        doctorService.cancelBooking(currentUser.getId(), bookingId, reason);
        return ApiResponse.success(HttpStatus.OK, "Hủy lịch hẹn thành công.");
    }

    // 4. Đánh dấu hoàn thành
    @PatchMapping("/bookings/{bookingId}/complete")
    public ResponseEntity<ApiResponse<Object>> completeBooking(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long bookingId) {

        doctorService.completeBooking(currentUser.getId(), bookingId);
        return ApiResponse.success(HttpStatus.OK, "Đánh dấu lịch hẹn đã hoàn thành.");
    }
}