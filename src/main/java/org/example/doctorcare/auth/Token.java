package org.example.doctorcare.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.doctorcare.user.User;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, length = 500)
    private String token;

    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}