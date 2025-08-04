package org.example.doctorcare.admin;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.doctorcare.admin.dto.*;
import org.example.doctorcare.common.ApiResponse;
import org.example.doctorcare.doctor.dto.BookingDetailForDoctorDTO;
import org.example.doctorcare.public_api.booking.dto.BookingHistoryDTO;
import org.example.doctorcare.public_api.clinic.dto.ClinicDTO;
import org.example.doctorcare.public_api.clinic.dto.CreateClinicRequest;
import org.example.doctorcare.public_api.clinic.dto.UpdateClinicRequest;
import org.example.doctorcare.public_api.specialization.dto.CreateSpecializationRequest;
import org.example.doctorcare.public_api.specialization.dto.SpecializationDTO;
import org.example.doctorcare.public_api.specialization.dto.UpdateSpecializationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Management", description = "Các API dành cho Quản trị viên hệ thống")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    // --- API cho Chuyên khoa ---

    @PostMapping("/specializations")
    public ResponseEntity<ApiResponse<SpecializationDTO>> createSpecialization(
            @Valid @RequestBody CreateSpecializationRequest request) {
        SpecializationDTO newSpec = adminService.createSpecialization(request);
        return ApiResponse.success(HttpStatus.CREATED, "Tạo chuyên khoa mới thành công.", newSpec);
    }

    @GetMapping("/specializations")
    public ResponseEntity<ApiResponse<Page<SpecializationDTO>>> getAllSpecializations(Pageable pageable) {
        Page<SpecializationDTO> specializations = adminService.findAllSpecializations(pageable);
        return ApiResponse.success(HttpStatus.OK, "Lấy danh sách chuyên khoa thành công.", specializations);
    }

    @PutMapping("/specializations/{id}")
    public ResponseEntity<ApiResponse<SpecializationDTO>> updateSpecialization(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSpecializationRequest request) {
        SpecializationDTO updatedSpec = adminService.updateSpecialization(id, request);
        return ApiResponse.success(HttpStatus.OK, "Cập nhật chuyên khoa thành công.", updatedSpec);
    }

    @DeleteMapping("/specializations/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSpecialization(@PathVariable Integer id) {
        adminService.deleteSpecialization(id);
        return ApiResponse.success(HttpStatus.OK, "Xóa chuyên khoa thành công.");
    }

    @PostMapping("/clinics")
    public ResponseEntity<ApiResponse<ClinicDTO>> createClinic(
            @Valid @RequestBody CreateClinicRequest request) {
        ClinicDTO newClinic = adminService.createClinic(request);
        return ApiResponse.success(HttpStatus.CREATED, "Tạo phòng khám mới thành công.", newClinic);
    }

    @GetMapping("/clinics")
    public ResponseEntity<ApiResponse<Page<ClinicDTO>>> getAllClinics(Pageable pageable) {
        Page<ClinicDTO> clinics = adminService.findAllClinics(pageable);
        return ApiResponse.success(HttpStatus.OK, "Lấy danh sách phòng khám thành công.", clinics);
    }

    @PutMapping("/clinics/{id}")
    public ResponseEntity<ApiResponse<ClinicDTO>> updateClinic(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClinicRequest request) {
        ClinicDTO updatedClinic = adminService.updateClinic(id, request);
        return ApiResponse.success(HttpStatus.OK, "Cập nhật phòng khám thành công.", updatedClinic);
    }

    @DeleteMapping("/clinics/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteClinic(@PathVariable Long id) {
        adminService.deleteClinic(id);
        return ApiResponse.success(HttpStatus.OK, "Xóa phòng khám thành công.");
    }
    @PostMapping("/doctors")
    public ResponseEntity<ApiResponse<Object>> createDoctorAccount(
            @Valid @RequestBody CreateDoctorRequest request) {
        adminService.createDoctor(request);
        return ApiResponse.success(HttpStatus.CREATED, "Tạo tài khoản bác sĩ thành công.");
    }

    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse<Page<DoctorListItemDTO>>> getAllDoctors(Pageable pageable) {
        Page<DoctorListItemDTO> doctors = adminService.findAllDoctors(pageable);
        return ApiResponse.success(HttpStatus.OK, "Lấy danh sách bác sĩ thành công.", doctors);
    }

    @PutMapping("/doctors/{doctorId}")
    public ResponseEntity<ApiResponse<Object>> updateDoctorInfo(
            @PathVariable Long doctorId,
            @Valid @RequestBody UpdateDoctorInfoRequest request) {
        adminService.updateDoctorInfo(doctorId, request);
        return ApiResponse.success(HttpStatus.OK, "Cập nhật thông tin bác sĩ thành công.");
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserManagementDTO>>> getAllUsers(Pageable pageable) {
        Page<UserManagementDTO> users = adminService.findAllUsers(pageable);
        return ApiResponse.success(HttpStatus.OK, "Lấy danh sách người dùng thành công.", users);
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Object>> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        adminService.updateUserStatus(userId, request);
        String message = request.getIsActive() ? "Mở khóa tài khoản thành công." : "Khóa tài khoản thành công.";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @GetMapping("/bookings/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<BookingHistoryDTO>>> getBookingHistoryByPatientId(@PathVariable Long patientId) {
        List<BookingHistoryDTO> history = adminService.getBookingHistoryForPatient(patientId);
        String message = "Lấy lịch sử đặt khám của bệnh nhân ID " + patientId + " thành công.";
        return ApiResponse.success(HttpStatus.OK, message, history);
    }

    @GetMapping("/bookings/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<BookingDetailForDoctorDTO>>> getBookingsByDoctorId(@PathVariable Long doctorId) {
        List<BookingDetailForDoctorDTO> bookings = adminService.getBookingsForDoctor(doctorId);
        String message = "Lấy danh sách lịch hẹn của bác sĩ ID " + doctorId + " thành công.";
        return ApiResponse.success(HttpStatus.OK, message, bookings);
    }
}