package org.example.doctorcare.public_api.clinic;

import org.example.doctorcare.public_api.clinic.dto.TopClinicDTO;
import org.example.doctorcare.public_api.specialization.TopSpecializationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface ClinlicRepository extends JpaRepositoryImplementation<Clinic,Long> {

    @Query("SELECT new org.example.doctorcare.public_api.clinic.dto.TopClinicDTO(" +
            "c.id, c.name, c.address, c.image, COUNT(b.id)) " +
            "FROM Clinic c " +
            "LEFT JOIN DoctorUser du ON c.id = du.clinic.id " +
            "LEFT JOIN User d ON du.doctor.id = d.id " +
            "LEFT JOIN Comment b ON b.doctor.id = d.id " + // Giả sử Entity đặt lịch là Comment
            "GROUP BY c.id, c.name, c.address, c.image " +
            "ORDER BY COUNT(b.id) DESC, c.name ASC")
    List<TopClinicDTO> findTopClinics(Pageable pageable);

}
