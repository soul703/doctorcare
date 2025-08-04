package org.example.doctorcare.doctor.dto;


import lombok.Data;
import org.example.doctorcare.schedule.Schedule;

// DTO chứa thông tin tóm tắt của một lịch làm việc
@Data
public class DoctorScheduleDTO {
    private Long id;
    private String date;
    private String time;
    private boolean isAvailable; // true nếu còn chỗ

    public static DoctorScheduleDTO fromEntity(Schedule schedule) {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setTime(schedule.getTime());
        // So sánh sumBooking và maxBooking để xác định còn chỗ không
        int sum = Integer.parseInt(schedule.getSumBooking() != null ? schedule.getSumBooking() : "0");
        int max = Integer.parseInt(schedule.getMaxBooking());
        dto.setAvailable(sum < max);
        return dto;
    }
}