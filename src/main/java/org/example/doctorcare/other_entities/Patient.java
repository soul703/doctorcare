package org.example.doctorcare.other_entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.doctorcare.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctorId")
    @ToString.Exclude
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statusId")
    @ToString.Exclude
    private Status status;

    @Column(name = "name", length = 255)
    private String name;

    // Các trường createdAt, updatedAt, deletedAt có trong schema gốc không?
    // Nếu có, bạn có thể thêm chúng vào giống như các entity khác.
}