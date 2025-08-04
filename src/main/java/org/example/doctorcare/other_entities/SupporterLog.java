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
@Table(name = "supporterlogs")
@Getter
@Setter
public class SupporterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patientId")
    @ToString.Exclude
    private User patient; // Giả sử patientId tham chiếu đến bảng users

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supporterId")
    @ToString.Exclude
    private User supporter; // Giả sử supporterId cũng tham chiếu đến bảng users

    @Column(name = "content", length = 255)
    private String content;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
