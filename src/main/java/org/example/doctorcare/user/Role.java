package org.example.doctorcare.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "ROLE_ADMIN", "ROLE_DOCTOR", "ROLE_USER"

    @OneToMany(mappedBy = "role")
    private Set<User> users;
}