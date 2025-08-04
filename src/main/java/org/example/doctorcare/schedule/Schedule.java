package org.example.doctorcare.schedule;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.example.doctorcare.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctorId")
    @ToString.Exclude
    @JsonManagedReference("patient-bookings")
    private User doctor;

    @Column(name = "date", length = 255)
    private String date; // Giữ nguyên kiểu String để tuân thủ schema

    @Column(name = "time", length = 255)
    private String time;

    @Column(name = "maxBooking", length = 255)
    private String maxBooking;

    @Column(name = "sumBooking", length = 255)
    private String sumBooking;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
