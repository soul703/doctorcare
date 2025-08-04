package org.example.doctorcare.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.example.doctorcare.doctor.DoctorUser;
import org.example.doctorcare.public_api.booking.Comment;
import org.example.doctorcare.schedule.Schedule;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private String address;
    private String phone;
    private String avatar;
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // --- Mối quan hệ ---
    @ManyToOne(fetch = FetchType.EAGER) // EAGER để lấy thông tin role ngay khi lấy user
    @JoinColumn(name = "role_id")
    @ToString.Exclude // Tránh vòng lặp vô hạn khi gọi toString()
    private Role role;

    @OneToMany(mappedBy = "doctor") // Một bác sĩ có nhiều lịch làm việc
    @ToString.Exclude
    @JsonManagedReference("doctor-schedules")
    private Set<Schedule> schedules;

    @OneToMany(mappedBy = "doctor") // Một bác sĩ có nhiều lịch hẹn
    @ToString.Exclude
    @JsonManagedReference("doctor-bookings")
    private Set<Comment> doctorBookings;

    @OneToMany(mappedBy = "patient") // Một bệnh nhân có nhiều lịch hẹn
    @ToString.Exclude
    @JsonManagedReference("patient-bookings")
    private Set<Comment> patientBookings;

    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude // Rất quan trọng để tránh vòng lặp khi gọi toString()
    @JsonManagedReference("user-doctorinfo") // Dùng để xử lý vòng lặp JSON với Jackson
    private DoctorUser doctorInfo;
}
