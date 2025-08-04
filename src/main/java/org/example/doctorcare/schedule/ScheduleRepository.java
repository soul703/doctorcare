package org.example.doctorcare.schedule;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    // Trong ScheduleRepository.java
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Schedule s WHERE s.id = :id")
    Optional<Schedule> findByIdWithLock(Long id);

    Optional<Schedule> findByDoctorIdAndDateAndTime(Long doctorId, String date, String time);

    // Tìm lịch làm việc của bác sĩ từ một ngày trở đi
    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId AND s.date >= :startDate ORDER BY s.date, s.time")
    List<Schedule> findAvailableSchedules(Long doctorId, String startDate);
    boolean existsByDoctorIdAndDateAndTime(Long doctorId, String date, String time);
}
