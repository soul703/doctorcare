package org.example.doctorcare.doctor.dto;

import lombok.Data;
import org.example.doctorcare.public_api.booking.Comment;
import org.example.doctorcare.user.User;

@Data
public class BookingDetailForDoctorDTO {
    private Long bookingId;
    private String patientName;
    private String patientPhone;
    private String patientGender;
    private String bookingDate;
    private String bookingTime;
    private String status;

    public static BookingDetailForDoctorDTO fromEntity(Comment booking) {
        BookingDetailForDoctorDTO dto = new BookingDetailForDoctorDTO();
        dto.setBookingId(booking.getId());
        dto.setBookingDate(booking.getDateBooking());
        dto.setBookingTime(booking.getTimeBooking());
        dto.setStatus(booking.getStatus()); // Cần logic status chi tiết hơn

        User patient = booking.getPatient();
        if (patient != null) {
            dto.setPatientName(patient.getUsername());
            dto.setPatientPhone(patient.getPhone());
            dto.setPatientGender(patient.getGender());
        }

        return dto;
    }
}
