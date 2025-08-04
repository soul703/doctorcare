package org.example.doctorcare.public_api.booking.dto;

import lombok.Data;
import org.example.doctorcare.doctor.DoctorUser;
import org.example.doctorcare.public_api.booking.Comment;
import org.example.doctorcare.user.User;

@Data
public class BookingHistoryDTO {
    private Long bookingId;
    private String date;
    private String time;
    private String doctorName;
    private String clinicName;
    private String specializationName;
    private String status; // Trạng thái lịch hẹn

    public static BookingHistoryDTO fromEntity(Comment booking) { // hoặc Booking booking
        BookingHistoryDTO dto = new BookingHistoryDTO();
        dto.setBookingId(booking.getId());
        dto.setDate(booking.getDateBooking());
        dto.setTime(booking.getTimeBooking());
        dto.setStatus(booking.getStatus());

        User doctor = booking.getDoctor();
        if (doctor != null) {
            dto.setDoctorName(doctor.getUsername());
        }
        DoctorUser doctorUser = booking.getDoctor().getDoctorInfo();
        if (doctorUser != null) {
            dto.setClinicName(doctorUser.getClinic().getName());
            dto.setSpecializationName(doctorUser.getSpecialization() != null ? doctorUser.getSpecialization().getName() : null);
        }
        return dto;
    }
}
