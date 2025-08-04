package org.example.doctorcare.public_api.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.doctorcare.public_api.clinic.Clinic;
import org.example.doctorcare.public_api.specialization.Specialization;
import org.example.doctorcare.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "contentMarkdown", columnDefinition = "TEXT")
    private String contentMarkdown;

    @Column(name = "contentHTML", columnDefinition = "TEXT")
    private String contentHTML;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forDoctorId")
    @ToString.Exclude
    private User forDoctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forSpecializationId")
    @ToString.Exclude
    private Specialization forSpecialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forClinicId")
    @ToString.Exclude
    private Clinic forClinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writerId")
    @ToString.Exclude
    private User writer;

    @Column(name = "confirmByDoctor")
    private boolean confirmByDoctor;

    @Column(name = "image", length = 255)
    private String image;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}