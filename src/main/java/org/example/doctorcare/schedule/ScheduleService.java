package org.example.doctorcare.schedule;

import lombok.RequiredArgsConstructor;
import org.example.doctorcare.exception.custom.ConflictException;
import org.example.doctorcare.schedule.dto.CreateScheduleRequest;
import org.example.doctorcare.user.User;
import org.example.doctorcare.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository; // Để lấy đối tượng User của bác sĩ

    @Transactional
    public void createDoctorSchedule(Long doctorId, CreateScheduleRequest request) {
        // Kiểm tra xem lịch cho ngày và khung giờ này đã tồn tại cho bác sĩ này chưa
        boolean scheduleExists = scheduleRepository.existsByDoctorIdAndDateAndTime(
                doctorId,
                request.getDate().toString(), // Chuyển LocalDate sang String để khớp với Entity
                request.getTimeType()
        );

        if (scheduleExists) {
            throw new ConflictException("Lịch làm việc cho ngày và khung giờ này đã tồn tại.");
        }

        // Lấy đối tượng User của bác sĩ
        User doctor = userRepository.getReferenceById(doctorId);

        Schedule newSchedule = new Schedule();
        newSchedule.setDoctor(doctor);
        newSchedule.setDate(request.getDate().toString());
        newSchedule.setTime(request.getTimeType());
        newSchedule.setMaxBooking(String.valueOf(request.getMaxBooking()));
        newSchedule.setSumBooking("0"); // Khởi tạo số lượng đặt là 0

        scheduleRepository.save(newSchedule);
    }
}