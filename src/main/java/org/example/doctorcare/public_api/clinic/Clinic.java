package org.example.doctorcare.public_api.clinic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.doctorcare.user.BaseEntity;

@Entity
@Table(name = "clinics")
@Getter
@Setter
public class Clinic extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String address;
    private String phone;
    private String image;

    @Column(name = "introduction_html", columnDefinition = "TEXT")
    private String introductionHTML;

    @Column(name = "introduction_markdown", columnDefinition = "TEXT")
    private String introductionMarkdown;

    @Column(columnDefinition = "TEXT")
    private String description;
}