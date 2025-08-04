package org.example.doctorcare.public_api.booking;


import lombok.RequiredArgsConstructor;
import org.example.doctorcare.cache.CacheService;
import org.example.doctorcare.exception.custom.BadRequestException;
import org.example.doctorcare.exception.custom.ResourceNotFoundException;
import org.example.doctorcare.public_api.booking.dto.BookingHistoryDTO;
import org.example.doctorcare.public_api.booking.dto.CreateBookingRequest;
import org.example.doctorcare.schedule.Schedule;
import org.example.doctorcare.schedule.ScheduleRepository;
import org.example.doctorcare.user.User;
import org.example.doctorcare.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final CommentRepository commentRepository; // Giả sử Entity là Comment
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;


    private static final String TOP_SPECIALIZATIONS_CACHE_KEY_PREFIX = "cache:specializations:top:";
    private static final String TOP_CLINICS_CACHE_KEY_PREFIX = "cache:clinics:top:";


        @Transactional
        public void createBooking(Long patientId, CreateBookingRequest request) {
            // 1. Dùng Pessimistic Lock để khóa bản ghi Schedule, tránh race condition
            Schedule schedule = scheduleRepository.findByIdWithLock(request.getScheduleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch khám với ID: " + request.getScheduleId()));

            // 2. Kiểm tra nghiệp vụ
            if (schedule.getDoctor().getId() != request.getDoctorId()) {
                throw new BadRequestException("Lịch khám không thuộc về bác sĩ được chọn.");
            }

            Integer currentBooking = Integer.parseInt(schedule.getSumBooking() != null ? schedule.getSumBooking() : "0");
            Integer maxBooking = Integer.parseInt(schedule.getMaxBooking());

            if (currentBooking >= maxBooking) {
                throw new BadRequestException("Lịch khám cho khung giờ này đã đầy. Vui lòng chọn khung giờ khác.");
            }

            // 3. Cập nhật lịch làm việc
            schedule.setSumBooking(String.valueOf(currentBooking + 1));
            scheduleRepository.save(schedule);

            // 4. Tạo lịch hẹn mới
            User patient = userRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + patientId));

            Comment newBooking = new Comment();
            newBooking.setStatus(BookingStatus.NEW);
            newBooking.setDoctor(schedule.getDoctor());
            newBooking.setPatient(patient); // Cần thêm trường patient vào Entity Comment/Booking
            newBooking.setDateBooking(schedule.getDate());
            newBooking.setTimeBooking(schedule.getTime());
            //... set các trường khác từ request

            commentRepository.save(newBooking);

            // 5. Vô hiệu hóa cache liên quan
            invalidateTopDataCache();
        }
    private void invalidateTopDataCache() {
        // Xóa cache cho các limit phổ biến
        cacheService.evict(TOP_SPECIALIZATIONS_CACHE_KEY_PREFIX + 4);
        cacheService.evict(TOP_SPECIALIZATIONS_CACHE_KEY_PREFIX + 8);
        cacheService.evict(TOP_CLINICS_CACHE_KEY_PREFIX + 4);
        cacheService.evict(TOP_CLINICS_CACHE_KEY_PREFIX + 8);
    }
    @Transactional(readOnly = true)
    public List<BookingHistoryDTO> getBookingHistoryForPatient(Long patientId) {
        // 1. Kiểm tra quyền truy cập
        List<Comment> bookings = commentRepository.findByPatient_IdOrderByDateBookingDesc(patientId);


        return bookings.stream()
                .map(BookingHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }
    @Transactional
    public void cancelBookingByPatient(Long patientId, Long bookingId) {
        // 1. Tìm lịch hẹn
        Comment booking = commentRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn với ID: " + bookingId));

        // 2. Kiểm tra quyền sở hữu
        if (!booking.getPatient().getId().equals(patientId)) {
            throw new AccessDeniedException("Bạn không có quyền hủy lịch hẹn này.");
        }

        // 3. Kiểm tra trạng thái có cho phép hủy không (ví dụ: chỉ được hủy khi trạng thái là NEW)
        // if (!"NEW".equals(booking.getStatus())) {
        //     throw new BadRequestException("Không thể hủy lịch hẹn đã được xác nhận hoặc đã qua.");
        // }

        // 4. Cập nhật trạng thái
        booking.setStatus(BookingStatus.CANCELLED_BY_PATIENT); // Hoặc một trạng thái "CANCELLED_BY_PATIENT"
        commentRepository.save(booking);

        // 5. Trả lại suất khám
        // Tìm schedule tương ứng để giảm sumBooking
        scheduleRepository.findByDoctorIdAndDateAndTime(
                booking.getDoctor().getId(),
                booking.getDateBooking(),
                booking.getTimeBooking()
        ).ifPresent(schedule -> {
            int currentBooking = Integer.parseInt(schedule.getSumBooking());
            if (currentBooking > 0) {
                schedule.setSumBooking(String.valueOf(currentBooking - 1));
                scheduleRepository.save(schedule);
            }
        });

        // 6. Xóa cache
        invalidateTopDataCache();
    }
    }
