package org.example.doctorcare.public_api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.doctorcare.cache.CacheService;
import org.example.doctorcare.doctor.DoctorUser;
import org.example.doctorcare.doctor.dto.DoctorProfileDTO;
import org.example.doctorcare.doctor.dto.DoctorUserRepository;
import org.example.doctorcare.exception.custom.ResourceNotFoundException;
import org.example.doctorcare.public_api.clinic.ClinlicRepository;
import org.example.doctorcare.public_api.clinic.dto.TopClinicDTO;
import org.example.doctorcare.public_api.specialization.SpecializationRepository;
import org.example.doctorcare.public_api.specialization.TopSpecializationDTO;
import org.example.doctorcare.schedule.Schedule;
import org.example.doctorcare.schedule.ScheduleRepository;
import org.example.doctorcare.user.User;
import org.example.doctorcare.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicApiService {

    private final SpecializationRepository specializationRepository;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;
    private final DoctorUserRepository doctorUserRepository;
    private final UserRepository userRepository;
    private  final ScheduleRepository scheduleRepository        ;
    private final ClinlicRepository clinicRepository;

    // Best Practice: Định nghĩa hằng số cho key cache để tránh lỗi chính tả
    private static final String TOP_SPECIALIZATIONS_CACHE_KEY_PREFIX = "cache:specializations:top:";
    private static final String TOP_CLINICS_CACHE_KEY_PREFIX = "cache:clinics:top:";
    /**
     * Lấy danh sách các phòng khám nổi bật, có áp dụng caching.
     * @param limit Số lượng phòng khám cần lấy.
     * @return Danh sách DTO của các phòng khám nổi bật.
     */
    @Transactional(readOnly = true)
    public List<TopClinicDTO> getTopClinics(int limit) {
        final String cacheKey = TOP_CLINICS_CACHE_KEY_PREFIX + limit;

        // 1. Kiểm tra cache
        Object cachedData = cacheService.get(cacheKey);
        if (cachedData != null) {
            log.info("CACHE HIT: Found data for key '{}'", cacheKey);
            return objectMapper.convertValue(cachedData, new TypeReference<>() {});
        }

        // 2. Cache miss: Truy vấn database
        log.warn("CACHE MISS: No data found for key '{}'. Fetching from database.", cacheKey);
        Pageable pageable = PageRequest.of(0, limit);
        List<TopClinicDTO> topClinics = clinicRepository.findTopClinics(pageable);

        // 3. Lưu kết quả vào cache nếu có
        if (topClinics != null && !topClinics.isEmpty()) {
            cacheService.set(cacheKey, topClinics, 1, TimeUnit.HOURS);
        }

        return topClinics;
    }
    @Transactional(readOnly = true)
    public List<TopSpecializationDTO> getTopSpecializations(int limit) {
        // Tạo key cache động dựa trên tham số
        final String cacheKey = TOP_SPECIALIZATIONS_CACHE_KEY_PREFIX + limit;

        // 1. Kiểm tra cache
        Object cachedData = cacheService.get(cacheKey);
        if (cachedData != null) {
            log.info("CACHE HIT: Found data for key '{}'", cacheKey);
            // Dùng ObjectMapper để chuyển đổi kiểu dữ liệu an toàn
            return objectMapper.convertValue(cachedData, new TypeReference<>() {});
        }

        // 2. Cache miss: Truy vấn database
        log.warn("CACHE MISS: No data found for key '{}'. Fetching from database.", cacheKey);
        Pageable pageable = PageRequest.of(0, limit);
        List<TopSpecializationDTO> topSpecializations = specializationRepository.findTopSpecializations(pageable);

        // 3. Lưu kết quả vào cache nếu có dữ liệu
        if (topSpecializations != null && !topSpecializations.isEmpty()) {
            // Đặt TTL là 1 giờ. Sau 1 giờ, cache sẽ tự động hết hạn.
            cacheService.set(cacheKey, topSpecializations, 1, TimeUnit.HOURS);
        }

        return topSpecializations;
    }
    @Transactional(readOnly = true)
    public DoctorProfileDTO getDoctorDetails(Long doctorId) {
        // 1. Lấy thông tin cơ bản của user (bác sĩ)
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ với ID: " + doctorId));

        // 2. Lấy thông tin chuyên môn (chuyên khoa, phòng khám)
        DoctorUser doctorInfo = doctorUserRepository.findByDoctor_Id(doctorId).orElse(null);

        // 3. Lấy lịch làm việc trong tương lai (ví dụ: từ hôm nay)
        String today = LocalDate.now().toString();
        List<Schedule> schedules = scheduleRepository.findAvailableSchedules(doctorId, today);

        // 4. Tạo DTO để trả về
        return DoctorProfileDTO.fromEntities(doctor, doctorInfo, schedules);
    }
    @Transactional(readOnly = true)
    public Page<SearchResultDTO> search(SearchRequest request, Pageable pageable) {
        // Tạo Specification từ keyword
        Specification<DoctorUser> spec = DoctorUserSpecification.searchByKeyword(request.getKeyword());
        List<String> allowedSortFields = List.of("id", "createdAt", "updatedAt");

        Sort safeSort = pageable.getSort().stream()
                .filter(order -> allowedSortFields.contains(order.getProperty()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Sort::by));

        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                safeSort
        );

        // Thực hiện truy vấn với Specification và phân trang
        Page<DoctorUser> results = doctorUserRepository.findAll(spec, safePageable);

        // Chuyển đổi từ Page<Entity> sang Page<DTO>
        return results.map(SearchResultDTO::fromEntity);
    }
}