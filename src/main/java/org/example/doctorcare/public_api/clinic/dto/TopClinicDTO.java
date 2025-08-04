package org.example.doctorcare.public_api.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Quan trọng cho câu lệnh SELECT new trong JPQL
public class TopClinicDTO {
    private Long id;
    private String name;
    private String address;
    private String image;
    private Long bookingCount;
}