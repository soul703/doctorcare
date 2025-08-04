package org.example.doctorcare.public_api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.doctorcare.common.ApiResponse;
import org.example.doctorcare.doctor.dto.DoctorProfileDTO;
import org.example.doctorcare.public_api.clinic.dto.TopClinicDTO;
import org.example.doctorcare.public_api.specialization.TopSpecializationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
@Tag(name = "Public API", description = "Các API công khai dành cho người dùng")
public class PublicController {

    private final PublicApiService publicApiService;
    // ...

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorProfileDTO>> getDoctorDetails(@PathVariable Long doctorId) {
        DoctorProfileDTO doctorDetails = publicApiService.getDoctorDetails(doctorId);
        return ApiResponse.success(HttpStatus.OK, "Lấy thông tin chi tiết bác sĩ thành công", doctorDetails);
    }
    /**
     * API để lấy danh sách các cơ sở y tế/phòng khám nổi bật.
     * @param limit Số lượng phòng khám muốn lấy, mặc định là 4.
     * @return ApiResponse chứa danh sách các phòng khám.
     */
    @GetMapping("/clinics/top")
    public ResponseEntity<ApiResponse<List<TopClinicDTO>>> getTopClinics(
            @RequestParam(defaultValue = "4") int limit) {

        List<TopClinicDTO> topClinics = publicApiService.getTopClinics(limit);

        String message = "Lấy danh sách phòng khám nổi bật thành công.";
        return ApiResponse.success(HttpStatus.OK, message, topClinics);
    }
    @GetMapping("/top-specialties")
    public ResponseEntity<ApiResponse<List<TopSpecializationDTO>>> getTopSpecialties(@RequestParam int limit) {
        List<TopSpecializationDTO> topSpecialties = publicApiService.getTopSpecializations(limit);
        return ApiResponse.success(HttpStatus.OK, "Lấy danh sách chuyên khoa hàng đầu thành công", topSpecialties);
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<SearchResultDTO>>> search(
            @RequestBody SearchRequest request,
            Pageable pageable) {

        Page<SearchResultDTO> results = publicApiService.search(request, pageable);
        return ApiResponse.success(HttpStatus.OK, "Tìm kiếm thành công.", results);
    }
}
