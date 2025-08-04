package org.example.doctorcare.auth.dto;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import để ẩn phương thức isPasswordMatching

@Data
public class RegisterRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    private String name;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 20, message = "Mật khẩu phải có từ 6 đến 20 ký tự")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String confirmPassword;


    // Phương thức này sẽ được Bean Validation tự động gọi
    @JsonIgnore // Bỏ qua phương thức này khi serialize/deserialize JSON
    @AssertTrue(message = "Mật khẩu và nhập lại mật khẩu không khớp")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}