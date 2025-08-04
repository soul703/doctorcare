package org.example.doctorcare.public_api;

import jakarta.persistence.criteria.Join;
import org.example.doctorcare.doctor.DoctorUser;
import org.example.doctorcare.public_api.clinic.Clinic;
import org.example.doctorcare.public_api.specialization.Specialization;
import org.example.doctorcare.user.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class DoctorUserSpecification {

    public static Specification<DoctorUser> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            // Nếu keyword rỗng, không áp dụng điều kiện lọc nào
            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }

            // Chuẩn hóa keyword để tìm kiếm không phân biệt hoa thường và chứa ký tự
            String likePattern = "%" + keyword.toLowerCase() + "%";

            // Join đến các bảng liên quan
            Join<DoctorUser, User> doctorJoin = root.join("doctor");
            Join<DoctorUser, Specialization> specJoin = root.join("specialization");
            Join<DoctorUser, Clinic> clinicJoin = root.join("clinic");

            // Tạo các điều kiện OR
            // 1. Tìm theo tên bác sĩ
            var doctorNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(doctorJoin.get("username")), likePattern);
            // 2. Tìm theo tên chuyên khoa
            var specNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(specJoin.get("name")), likePattern);
            // 3. Tìm theo tên phòng khám
            var clinicNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(clinicJoin.get("name")), likePattern);

            // Kết hợp các điều kiện lại với nhau bằng OR
            return criteriaBuilder.or(doctorNamePredicate, specNamePredicate, clinicNamePredicate);
        };
    }
}