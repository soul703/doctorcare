package org.example.doctorcare.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDoctorRequest {
    // Thông tin cho bảng 'users'
    @NotBlank(message = "Tên bác sĩ không được để trống")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    private String address;
    private String phone;
    private String gender;

    // Thông tin cho bảng 'doctor_users'
    @NotNull(message = "ID phòng khám không được để trống")
    private Integer clinicId;

    @NotNull(message = "ID chuyên khoa không được để trống")
    private Integer specializationId;

    // Thông tin mô tả thêm
    private String description;
}