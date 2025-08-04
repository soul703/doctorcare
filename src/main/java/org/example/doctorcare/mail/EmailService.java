package org.example.doctorcare.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.doctorcare.doctor.DoctorUser;
import org.example.doctorcare.public_api.booking.Comment;
import org.example.doctorcare.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Gửi email reset mật khẩu.
     * Sử dụng @Async để thực hiện việc gửi mail trong một luồng riêng,
     * giúp API trả về response ngay lập tức mà không cần chờ gửi mail xong.
     * @param to Email người nhận
     * @param name Tên người nhận
     * @param resetUrl URL để reset mật khẩu
     * @throws MessagingException
     */
    @Async
    public void sendPasswordResetEmail(String to, String name, String resetUrl) throws MessagingException {
        // Chuẩn bị nội dung email từ template Thymeleaf
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetUrl", resetUrl);
        String htmlContent = templateEngine.process("password-reset-email", context);

        // Tạo và gửi MimeMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Yêu cầu Đặt lại Mật khẩu cho tài khoản DoctorCare của bạn");
        helper.setText(htmlContent, true); // true để chỉ định đây là nội dung HTML

        mailSender.send(mimeMessage);
    }
    @Async
    public void sendBookingConfirmationEmail(Comment booking) throws MessagingException {
        User patient = booking.getPatient();
        User doctor = booking.getDoctor();
        // Cần lấy thông tin phòng khám. Giả sử DoctorUser được EAGER load hoặc JOIN
        DoctorUser doctorInfo = doctor.getDoctorInfo(); // Cần thêm mối quan hệ OneToOne này trong User

        Context context = new Context();
        context.setVariable("patientName", patient.getUsername());
        context.setVariable("doctorName", doctor.getUsername());
        context.setVariable("bookingDate", booking.getDateBooking());
        context.setVariable("bookingTime", booking.getTimeBooking());
        context.setVariable("clinicName", doctorInfo.getClinic().getName());
        context.setVariable("clinicAddress", doctorInfo.getClinic().getAddress());

        String htmlContent = templateEngine.process("booking-confirmed-email", context);
        sendHtmlEmail(patient.getEmail(), "Lịch hẹn của bạn đã được xác nhận", htmlContent);
    }

    @Async
    public void sendBookingCancellationEmail(Comment booking, String reason) throws MessagingException {
        User patient = booking.getPatient();

        Context context = new Context();
        context.setVariable("patientName", patient.getUsername());
        context.setVariable("doctorName", booking.getDoctor().getUsername());
        context.setVariable("bookingDate", booking.getDateBooking());
        context.setVariable("bookingTime", booking.getTimeBooking());
        context.setVariable("reason", reason);

        String htmlContent = templateEngine.process("booking-cancelled-email", context);
        sendHtmlEmail(patient.getEmail(), "Thông báo hủy lịch hẹn", htmlContent);
    }
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
    @Async
    public void sendWelcomeEmail(User user) throws MessagingException {
        // Chuẩn bị các biến cho template
        Context context = new Context();
        context.setVariable("name", user.getUsername());
        // URL này nên được lấy từ file config để linh hoạt giữa các môi trường
        context.setVariable("loginUrl", "http://localhost:3000/login");

        // Xử lý template
        String htmlContent = templateEngine.process("welcome-email", context);

        // Gửi email
        sendHtmlEmail(user.getEmail(), "Chào mừng bạn đến với DoctorCare", htmlContent);
    }
    // Bạn có thể thêm các phương thức gửi email khác ở đây (ví dụ: email chào mừng)
}
