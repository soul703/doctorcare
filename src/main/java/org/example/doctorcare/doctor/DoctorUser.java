package org.example.doctorcare.doctor;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.example.doctorcare.public_api.clinic.Clinic;
import org.example.doctorcare.public_api.specialization.Specialization;
import org.example.doctorcare.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_users")
@Data
public class DoctorUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinicId")
    @ToString.Exclude
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specializationId")
    @ToString.Exclude
    private Specialization specialization;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctorId")
    @ToString.Exclude
    @JsonBackReference("user-doctorinfo") // Tên phải khớp với @JsonManagedReference
    private User doctor;
}