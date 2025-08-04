package org.example.doctorcare.admin;

import lombok.RequiredArgsConstructor;
import org.example.doctorcare.admin.dto.*;
import org.example.doctorcare.auth.RefreshTokenService;
import org.example.doctorcare.cache.CacheService;
import org.example.doctorcare.doctor.DoctorService;
import org.example.doctorcare.doctor.DoctorUser;
import org.example.doctorcare.doctor.dto.BookingDetailForDoctorDTO;
import org.example.doctorcare.doctor.dto.DoctorUserRepository;
import org.example.doctorcare.exception.custom.ConflictException;
import org.example.doctorcare.exception.custom.ResourceNotFoundException;
import org.example.doctorcare.public_api.booking.BookingService;
import org.example.doctorcare.public_api.booking.dto.BookingHistoryDTO;
import org.example.doctorcare.public_api.clinic.Clinic;
import org.example.doctorcare.public_api.clinic.ClinlicRepository;
import org.example.doctorcare.public_api.clinic.dto.ClinicDTO;
import org.example.doctorcare.public_api.clinic.dto.CreateClinicRequest;
import org.example.doctorcare.public_api.clinic.dto.UpdateClinicRequest;
import org.example.doctorcare.public_api.specialization.Specialization;
import org.example.doctorcare.public_api.specialization.SpecializationRepository;
import org.example.doctorcare.public_api.specialization.dto.CreateSpecializationRequest;
import org.example.doctorcare.public_api.specialization.dto.SpecializationDTO;
import org.example.doctorcare.public_api.specialization.dto.UpdateSpecializationRequest;
import org.example.doctorcare.user.Role;
import org.example.doctorcare.user.RoleRepository;
import org.example.doctorcare.user.User;
import org.example.doctorcare.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final String TOP_SPECIALIZATIONS_CACHE_KEY_PREFIX = "cache:specializations:top:";
    private static final String TOP_CLINICS_CACHE_KEY_PREFIX = "cache:clinics:top:";



    private final SpecializationRepository specializationRepository;
    private final CacheService cacheService;
    private final ClinlicRepository clinicRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorUserRepository doctorUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final DoctorService doctorService;
    private final BookingService bookingService;

    // =================================================================
    // == QUẢN LÝ CHUYÊN KHOA (SPECIALIZATION)
    // =================================================================

    /**
     * Tạo chuyên khoa mới.
     *
     * @param request Thông tin chuyên khoa mới
     * @return SpecializationDTO đối tượng chuyên khoa vừa tạo
     */
    @Transactional
    public SpecializationDTO createSpecialization(CreateSpecializationRequest request) {
        Specialization newSpec = new Specialization();
        newSpec.setName(request.getName());
        newSpec.setDescription(request.getDescription());
        newSpec.setImage(request.getImage());

        Specialization savedSpec = specializationRepository.save(newSpec);

        // Vô hiệu hóa cache vì dữ liệu đã thay đổi
        invalidateTopSpecializationsCache();

        return SpecializationDTO.fromEntity(savedSpec);
    }

    /**
     * Lấy danh sách tất cả chuyên khoa với phân trang.
     *
     * @param pageable Thông tin phân trang
     * @return Page chứa danh sách chuyên khoa
     */
    @Transactional(readOnly = true)
    public Page<SpecializationDTO> findAllSpecializations(Pageable pageable) {
        return specializationRepository.findAll(pageable)
                .map(SpecializationDTO::fromEntity);
    }

    /**
     * Cập nhật thông tin chuyên khoa.
     *
     * @param id      ID của chuyên khoa cần cập nhật
     * @param request Thông tin cập nhật
     * @return SpecializationDTO đối tượng chuyên khoa đã cập nhật
     */
    @Transactional
    public SpecializationDTO updateSpecialization(Integer id, UpdateSpecializationRequest request) {
        Specialization spec = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + id));

        spec.setName(request.getName());
        spec.setDescription(request.getDescription());
        spec.setImage(request.getImage());

        Specialization updatedSpec = specializationRepository.save(spec);

        // Vô hiệu hóa cache vì dữ liệu đã thay đổi
        invalidateTopSpecializationsCache();

        return SpecializationDTO.fromEntity(updatedSpec);
    }

    /**
     * Xóa chuyên khoa theo ID.
     *
     * @param id ID của chuyên khoa cần xóa
     */
    @Transactional
    public void deleteSpecialization(Integer id) {
        if (!specializationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + id);
        }
        specializationRepository.deleteById(id);

        // Vô hiệu hóa cache vì dữ liệu đã thay đổi
        invalidateTopSpecializationsCache();
    }

    // Helper method để xóa cache
    private void invalidateTopSpecializationsCache() {
        // Xóa các key cache phổ biến
        cacheService.evict(TOP_SPECIALIZATIONS_CACHE_KEY_PREFIX + 4);
        cacheService.evict(TOP_SPECIALIZATIONS_CACHE_KEY_PREFIX + 8);
    }
    // =================================================================
    // == QUẢN LÝ PHÒNG KHÁM (CLINIC)
    // =================================================================

    /**
     * Tạo phòng khám mới.
     *
     * @param request Thông tin phòng khám mới
     * @return ClinicDTO đối tượng phòng khám vừa tạo
     */
    @Transactional
    public ClinicDTO createClinic(CreateClinicRequest request) {
        Clinic newClinic = new Clinic();
        newClinic.setName(request.getName());
        newClinic.setAddress(request.getAddress());
        newClinic.setPhone(request.getPhone());
        newClinic.setImage(request.getImage());
        newClinic.setDescription(request.getDescription());
        newClinic.setIntroductionHTML(request.getIntroductionHTML());
        newClinic.setIntroductionMarkdown(request.getIntroductionMarkdown());

        Clinic savedClinic = clinicRepository.save(newClinic);

        // Vô hiệu hóa cache vì dữ liệu đã thay đổi
        invalidateTopClinicsCache();

        return ClinicDTO.fromEntity(savedClinic);
    }

    /**
     * Lấy danh sách tất cả phòng khám với phân trang.
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ClinicDTO> findAllClinics(Pageable pageable) {
        return clinicRepository.findAll(pageable)
                .map(ClinicDTO::fromEntity);
    }

    /**
     * Cập nhật thông tin phòng khám.
     *
     * @param id      ID của phòng khám cần cập nhật
     * @param request Thông tin cập nhật
     * @return ClinicDTO đối tượng phòng khám đã cập nhật
     */
    @Transactional
    public ClinicDTO updateClinic(Long id, UpdateClinicRequest request) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng khám với ID: " + id));

        clinic.setName(request.getName());
        clinic.setAddress(request.getAddress());
        clinic.setPhone(request.getPhone());
        clinic.setImage(request.getImage());
        clinic.setDescription(request.getDescription());
        clinic.setIntroductionHTML(request.getIntroductionHTML());
        clinic.setIntroductionMarkdown(request.getIntroductionMarkdown());

        Clinic updatedClinic = clinicRepository.save(clinic);

        // Vô hiệu hóa cache vì dữ liệu đã thay đổi
        invalidateTopClinicsCache();

        return ClinicDTO.fromEntity(updatedClinic);
    }

    /**
     * Xóa phòng khám theo ID.
     *
     * @param id ID của phòng khám cần xóa
     */
    @Transactional
    public void deleteClinic(Long id) {
        if (!clinicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy phòng khám với ID: " + id);
        }
        // Kiểm tra xem có bác sĩ nào thuộc phòng khám này không
        //chua lamcai nay
        clinicRepository.deleteById(id);

        // Vô hiệu hóa cache vì dữ liệu đã thay đổi
        invalidateTopClinicsCache();
    }

    // ... các helper method đã có ...

    // Helper method để xóa cache của Clinic
    private void invalidateTopClinicsCache() {
        cacheService.evict(TOP_CLINICS_CACHE_KEY_PREFIX + 4);
        cacheService.evict(TOP_CLINICS_CACHE_KEY_PREFIX + 8);
    }

    // =================================================================
    // == QUẢN LÝ TÀI KHOẢN BÁC SĨ (DOCTOR)
    // =================================================================

    /**
     * Tạo tài khoản bác sĩ mới.
     *
     * @param request Thông tin bác sĩ mới
     */
    @Transactional
    public void createDoctor(CreateDoctorRequest request) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email này đã được sử dụng.");
        }

        // 2. Tìm các entity liên quan
        Role doctorRole = roleRepository.findByName("ROLE_DOCTOR")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy vai trò ROLE_DOCTOR."));
        Clinic clinic = clinicRepository.findById(Long.valueOf(request.getClinicId()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng khám với ID: " + request.getClinicId()));
        Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + request.getSpecializationId()));

        // 3. Tạo bản ghi User mới
        User newDoctorUser = new User();
        newDoctorUser.setUsername(request.getName());
        newDoctorUser.setEmail(request.getEmail());
        newDoctorUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newDoctorUser.setAddress(request.getAddress());
        newDoctorUser.setPhone(request.getPhone());
        newDoctorUser.setGender(request.getGender());
        newDoctorUser.setDescription(request.getDescription());
        newDoctorUser.setRole(doctorRole);
        newDoctorUser.setActive(true);
        User savedDoctor = userRepository.save(newDoctorUser);

        // 4. Tạo bản ghi DoctorUser để lưu thông tin chuyên môn
        DoctorUser doctorInfo = new DoctorUser();
        doctorInfo.setDoctor(savedDoctor);
        doctorInfo.setClinic(clinic);
        doctorInfo.setSpecialization(specialization);
        doctorUserRepository.save(doctorInfo);
    }

    /**
     * Lấy danh sách tất cả bác sĩ với phân trang.
     *
     * @param pageable Thông tin phân trang
     * @return Page chứa danh sách bác sĩ
     */
    @Transactional(readOnly = true)
    public Page<DoctorListItemDTO> findAllDoctors(Pageable pageable) {
        Role doctorRole = roleRepository.findByName("ROLE_DOCTOR")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy vai trò ROLE_DOCTOR."));
        return userRepository.findByRoleId(doctorRole.getId(), pageable)
                .map(DoctorListItemDTO::fromEntity);
    }

    /**
     * Cập nhật thông tin chuyên môn của bác sĩ.
     *
     * @param doctorId ID của bác sĩ cần cập nhật
     * @param request  Thông tin cập nhật
     */
    @Transactional
    public void updateDoctorInfo(Long doctorId, UpdateDoctorInfoRequest request) {
        // 1. Tìm bản ghi thông tin chuyên môn của bác sĩ
        DoctorUser doctorInfo = doctorUserRepository.findByDoctor_Id(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin chuyên môn cho bác sĩ ID: " + doctorId));

        // 2. Tìm các entity liên quan mới
        Clinic clinic = clinicRepository.findById(Long.valueOf(request.getClinicId()))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng khám với ID: " + request.getClinicId()));
        Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + request.getSpecializationId()));

        // 3. Cập nhật và lưu lại
        doctorInfo.setClinic(clinic);
        doctorInfo.setSpecialization(specialization);
        doctorUserRepository.save(doctorInfo);
    }
    // =================================================================
    // == QUẢN LÝ TÀI KHOẢN NGƯỜI DÙNG
    // =================================================================

    @Transactional(readOnly = true)
    public Page<UserManagementDTO> findAllUsers(Pageable pageable) {
        // Cần tải cả thông tin Role để tránh lỗi N+1
        // Tốt nhất là dùng @EntityGraph hoặc JOIN FETCH
        return userRepository.findAll(pageable).map(UserManagementDTO::fromEntity);
    }

    @Transactional
    public void updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        // Nếu trạng thái thay đổi thành không hoạt động (bị khóa)
        if (user.isActive() && !request.getIsActive()) {
            // Vô hiệu hóa các token hiện có của người dùng
            // Bước này đảm bảo người dùng đang đăng nhập sẽ bị buộc logout ở lần request tiếp theo
            // hoặc khi họ cố gắng refresh token.
            refreshTokenService.deleteByUserId(userId);

            // Việc blacklist access token phức tạp hơn và có thể bỏ qua nếu thời gian hết hạn ngắn.
            // Xóa refresh token đã là một biện pháp bảo mật rất mạnh.
        }

        user.setActive(request.getIsActive());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<BookingHistoryDTO> getBookingHistoryForPatient(Long patientId) {
        // Kiểm tra xem user có tồn tại không
        if (!userRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Không tìm thấy bệnh nhân với ID: " + patientId);
        }
        // Tái sử dụng logic đã có trong BookingService
        return bookingService.getBookingHistoryForPatient(patientId);
    }

    @Transactional(readOnly = true)
    public List<BookingDetailForDoctorDTO> getBookingsForDoctor(Long doctorId) {
        // Kiểm tra xem doctor có tồn tại không
        if (!userRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Không tìm thấy bác sĩ với ID: " + doctorId);
        }
        // Tái sử dụng logic đã có trong DoctorService
        return doctorService.getBookingsForDoctor(doctorId, null, null); // Lấy tất cả
    }
}