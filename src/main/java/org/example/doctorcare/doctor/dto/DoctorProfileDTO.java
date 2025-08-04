package org.example.doctorcare.doctor.dto;

import lombok.Data;
import org.example.doctorcare.doctor.DoctorUser;
import org.example.doctorcare.public_api.clinic.Clinic;
import org.example.doctorcare.public_api.specialization.Specialization;
import org.example.doctorcare.schedule.Schedule;
import org.example.doctorcare.user.User;

import java.util.List;

// DTO chứa thông tin chi tiết đầy đủ của một bác sĩ
@Data
public class DoctorProfileDTO {
    // Thông tin cơ bản
    private Long doctorId;
    private String name;
    private String avatar;
    private String description;

    // Thông tin phòng khám
    private Long clinicId;
    private String clinicName;
    private String clinicAddress;

    // Thông tin chuyên khoa
    private Long specializationId;
    private String specializationName;

    // Lịch làm việc
    private List<DoctorScheduleDTO> schedules;

    // Factory method để tạo DTO từ các entity
    public static DoctorProfileDTO fromEntities(User doctorUserEntity, DoctorUser doctorInfo, List<Schedule> schedules) {
        DoctorProfileDTO dto = new DoctorProfileDTO();

        // Từ User entity
        dto.setDoctorId(doctorUserEntity.getId());
        dto.setName(doctorUserEntity.getUsername());
        dto.setAvatar(doctorUserEntity.getAvatar());
        dto.setDescription(doctorUserEntity.getDescription());

        // Từ DoctorUser entity
        if (doctorInfo != null) {
            Clinic clinic = doctorInfo.getClinic();
            if (clinic != null) {
                dto.setClinicId(clinic.getId());
                dto.setClinicName(clinic.getName());
                dto.setClinicAddress(clinic.getAddress());
            }
            Specialization spec = doctorInfo.getSpecialization();
            if (spec != null) {
                dto.setSpecializationId(spec.getId());
                dto.setSpecializationName(spec.getName());
            }
        }

        // Từ danh sách Schedule
        if (schedules != null) {
            dto.setSchedules(schedules.stream().map(DoctorScheduleDTO::fromEntity).toList());
        }

        return dto;
    }
}