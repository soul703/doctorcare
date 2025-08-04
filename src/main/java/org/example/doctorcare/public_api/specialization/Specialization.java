package org.example.doctorcare.public_api.specialization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.doctorcare.user.BaseEntity;

@Entity
@Table(name = "specializations")
@Getter
@Setter
public class Specialization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String image;

    @Column(columnDefinition = "TEXT")
    private String description;
}