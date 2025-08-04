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
@Table(name = "extrainfos")
@Getter
@Setter
public class ExtraInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patientId")
    @ToString.Exclude
    private User patient; // Giả sử patientId tham chiếu đến bảng users

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placeId")
    @ToString.Exclude
    private Place place;

    @Column(name = "historyBreath", columnDefinition = "TEXT")
    private String historyBreath;

    @Column(name = "oldForms", columnDefinition = "TEXT")
    private String oldForms;

    @Column(name = "sendForms", columnDefinition = "TEXT")
    private String sendForms;

    @Column(name = "moreInfo", columnDefinition = "TEXT")
    private String moreInfo;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}