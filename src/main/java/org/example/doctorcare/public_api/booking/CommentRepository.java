package org.example.doctorcare.public_api.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> , JpaSpecificationExecutor<Comment> {
    List<Comment> findByPatient_IdOrderByDateBookingDesc(Long patientId);
}
