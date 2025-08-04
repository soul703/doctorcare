package org.example.doctorcare.public_api;

import lombok.Data;
import org.example.doctorcare.doctor.DoctorUser;

@Data
public class SearchResultDTO {
    private Long doctorId;
    private String doctorName;
    private String doctorAvatar;
    private String specializationName;
    private String clinicName;

    public static SearchResultDTO fromEntity(DoctorUser doctorUser) {
        SearchResultDTO dto = new SearchResultDTO();
        if (doctorUser.getDoctor() != null) {
            dto.setDoctorId(doctorUser.getDoctor().getId());
            dto.setDoctorName(doctorUser.getDoctor().getUsername());
            dto.setDoctorAvatar(doctorUser.getDoctor().getAvatar());
        }
        if (doctorUser.getSpecialization() != null) {
            dto.setSpecializationName(doctorUser.getSpecialization().getName());
        }
        if (doctorUser.getClinic() != null) {
            dto.setClinicName(doctorUser.getClinic().getName());
        }
        return dto;
    }
}