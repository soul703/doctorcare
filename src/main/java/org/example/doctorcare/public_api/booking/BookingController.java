package org.example.doctorcare.public_api.booking;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.doctorcare.common.ApiResponse;
import org.example.doctorcare.public_api.booking.dto.BookingHistoryDTO;
import org.example.doctorcare.public_api.booking.dto.CreateBookingRequest;
import org.example.doctorcare.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Tag(name = "Booking Management", description = "Các API liên quan đến đặt lịch khám bệnh")
@SecurityRequirement(name = "bearerAuth")
public class
BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createBooking(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateBookingRequest request) {

        bookingService.createBooking(currentUser.getId(), request);
        return ApiResponse.success(HttpStatus.CREATED, "Đặt lịch khám thành công. Vui lòng chờ xác nhận từ bác sĩ.");
    }
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<BookingHistoryDTO>>> getMyBookingHistory(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<BookingHistoryDTO> history = bookingService.getBookingHistoryForPatient(currentUser.getId());
        return ApiResponse.success(HttpStatus.OK, "Lấy lịch sử đặt khám thành công", history);
    }
    @PostMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Object>> cancelBooking(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long bookingId) {

        bookingService.cancelBookingByPatient(currentUser.getId(), bookingId);
        return ApiResponse.success(HttpStatus.OK, "Hủy lịch hẹn thành công.");
    }
}