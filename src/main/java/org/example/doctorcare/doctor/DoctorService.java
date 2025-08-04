package org.example.doctorcare.doctor;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.doctorcare.doctor.dto.BookingDetailForDoctorDTO;
import org.example.doctorcare.exception.custom.BadRequestException;
import org.example.doctorcare.exception.custom.ResourceNotFoundException;
import org.example.doctorcare.mail.EmailService;
import org.example.doctorcare.public_api.booking.BookingStatus;
import org.example.doctorcare.public_api.booking.Comment;
import org.example.doctorcare.public_api.booking.CommentRepository;
import org.example.doctorcare.schedule.ScheduleRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final CommentRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final EmailService emailService;


    // Chức năng 1: Xem danh sách lịch hẹn
    @Transactional(readOnly = true)
    public List<BookingDetailForDoctorDTO> getBookingsForDoctor(Long doctorId, String date, String status) {
        // Sử dụng Specification để tạo query động
        Specification<Comment> spec = Specification.where(BookingSpecifications.hasDoctorId(doctorId))
                .and(BookingSpecifications.hasDate(date))
                .and(BookingSpecifications.hasStatus(status));

        List<Comment> bookings = bookingRepository.findAll(spec);

        return bookings.stream()
                .map(BookingDetailForDoctorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Chức năng 2: Xác nhận lịch hẹn
    @Transactional
    public void confirmBooking(Long doctorId, Long bookingId) {
        log.info("Doctor {} attempting to confirm booking {}", doctorId, bookingId);
        Comment booking = findAndVerifyBooking(doctorId, bookingId);

        if (!BookingStatus.NEW.equals(booking.getStatus())) { // Giả sử status là String
            throw new BadRequestException("Chỉ có thể xác nhận lịch hẹn mới.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        try {
            emailService.sendBookingConfirmationEmail(booking);
        } catch (MessagingException e) {
            log.error("Failed to send confirmation email for booking {}", bookingId, e);
        }
    }

    // Chức năng 3: Hủy lịch hẹn
    @Transactional
    public void cancelBooking(Long doctorId, Long bookingId, String reason) {
        log.info("Doctor {} attempting to cancel booking {} with reason: {}", doctorId, bookingId, reason);
        Comment booking = findAndVerifyBooking(doctorId, bookingId);

        if (BookingStatus.COMPLETED.equals(booking.getStatus()) || BookingStatus.CANCELLED_BY_DOCTOR.equals(booking.getStatus())||BookingStatus.CANCELLED_BY_PATIENT.equals(booking.getStatus())) {
            throw new BadRequestException("Không thể hủy lịch hẹn đã hoàn thành hoặc đã bị hủy.");
        }

        // Cập nhật trạng thái booking
        booking.setStatus(BookingStatus.CANCELLED_BY_PATIENT);
        booking.setContent(booking.getContent() + "\nLý do hủy từ bác sĩ: " + reason); // Thêm lý do vào content
        bookingRepository.save(booking);

        // Trả lại suất khám
        scheduleRepository.findByDoctorIdAndDateAndTime(
                booking.getDoctor().getId(),
                booking.getDateBooking(),
                booking.getTimeBooking()
        ).ifPresent(schedule -> {
            int currentBooking = Integer.parseInt(schedule.getSumBooking());
            if (currentBooking > 0) {
                schedule.setSumBooking(String.valueOf(currentBooking - 1));
                scheduleRepository.save(schedule);
                log.info("Slot released for schedule {}", schedule.getId());
            }
        });
        try {
            emailService.sendBookingCancellationEmail(booking, reason);
        } catch (MessagingException e) {
            log.error("Failed to send cancellation email for booking {}", bookingId, e);
        }
    }

    // Chức năng 4: Đánh dấu hoàn thành
    @Transactional
    public void completeBooking(Long doctorId, Long bookingId) {
        log.info("Doctor {} attempting to complete booking {}", doctorId, bookingId);
        Comment booking = findAndVerifyBooking(doctorId, bookingId);

        if (!BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            throw new BadRequestException("Chỉ có thể hoàn thành lịch hẹn đã được xác nhận.");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
    }

    // Helper method để tái sử dụng logic tìm và xác thực booking
    private Comment findAndVerifyBooking(Long doctorId, Long bookingId) {
        Comment booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn với ID: " + bookingId));

        if (!booking.getDoctor().getId().equals(doctorId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập vào lịch hẹn này.");
        }
        return booking;
    }
}