package org.example.doctorcare.public_api.specialization;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {

    @Query("""
SELECT new org.example.doctorcare.public_api.specialization.TopSpecializationDTO(
    s.id, s.name, s.image, COUNT(b.id)
)
FROM Specialization s
LEFT JOIN DoctorUser du ON s.id = du.specialization.id
LEFT JOIN User d ON du.doctor.id = d.id
LEFT JOIN Comment b ON b.doctor.id = d.id
GROUP BY s.id, s.name, s.image
ORDER BY COUNT(b.id) DESC, s.name ASC
""")
    List<TopSpecializationDTO> findTopSpecializations(Pageable pageable);

}