package org.example.doctorcare.doctor;

import org.example.doctorcare.public_api.booking.Comment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
/**
 * BookingSpecifications.java
 * Chứa các Specification để lọc lịch hẹn theo bác sĩ, ngày và trạng thái.
 * Sử dụng trong việc truy vấn danh sách lịch hẹn của bác sĩ.
 */
public class BookingSpecifications {
    public static Specification<Comment> hasDoctorId(Long doctorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("doctor").get("id"), doctorId);
    }

    public static Specification<Comment> hasDate(String date) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(date)) {
                return criteriaBuilder.conjunction(); // Trả về điều kiện luôn đúng nếu date là null/rỗng
            }
            return criteriaBuilder.equal(root.get("dateBooking"), date);
        };
    }

    public static Specification<Comment> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(status)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}