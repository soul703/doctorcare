package org.example.doctorcare.doctor.dto;

import org.example.doctorcare.doctor.DoctorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorUserRepository extends JpaRepository<DoctorUser, Long>, JpaSpecificationExecutor<DoctorUser> {
    // Tìm thông tin bác sĩ dựa trên ID của User
    Optional<DoctorUser> findByDoctor_Id(Long doctorId);
}