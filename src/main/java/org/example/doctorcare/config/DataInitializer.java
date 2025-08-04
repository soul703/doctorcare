package org.example.doctorcare.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.doctorcare.doctor.DoctorUser;
import org.example.doctorcare.doctor.dto.DoctorUserRepository;
import org.example.doctorcare.public_api.booking.Comment;
import org.example.doctorcare.public_api.booking.CommentRepository;
import org.example.doctorcare.public_api.clinic.Clinic;
import org.example.doctorcare.public_api.clinic.ClinlicRepository;
import org.example.doctorcare.public_api.specialization.Specialization;
import org.example.doctorcare.public_api.specialization.SpecializationRepository;
import org.example.doctorcare.schedule.Schedule;
import org.example.doctorcare.schedule.ScheduleRepository;
import org.example.doctorcare.user.Role;
import org.example.doctorcare.user.RoleRepository;
import org.example.doctorcare.user.User;
import org.example.doctorcare.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClinlicRepository clinicRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorUserRepository doctorUserRepository;
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() > 0) {
            log.info("Data already initialized. Skipping.");
            return;
        }

        log.info("Initializing data...");

        // 1. Tạo Roles
        Role roleAdmin = createRole("ROLE_ADMIN");
        Role roleDoctor = createRole("ROLE_DOCTOR");
        Role roleUser = createRole("ROLE_USER");

        // 2. Tạo Users
        User admin = createUser("Admin", "admin@doctorcare.com", "admin123", "Hà Nội", "0111222333", "Male", roleAdmin);
        User doctor1 = createUser("Bác sĩ Trần Văn Hùng", "dr.hung@doctorcare.com", "doctor123", "TP. HCM", "0911222333", "Male", roleDoctor);
        User doctor2 = createUser("Bác sĩ Nguyễn Thị Lan", "dr.lan@doctorcare.com", "doctor123", "Đà Nẵng", "0922333444", "Female", roleDoctor);
        User patient1 = createUser("Nguyễn Văn An", "patient.an@gmail.com", "patient123", "Hải Phòng", "0988777666", "Male", roleUser);
        User patient2 = createUser("Trần Thị Bình", "patient.binh@gmail.com", "patient123", "Cần Thơ", "0977666555", "Female", roleUser);

        // 3. Tạo Chuyên khoa
        Specialization spec1 = createSpecialization("Cơ Xương Khớp", "Chuyên điều trị các bệnh về cơ, xương, khớp.");
        Specialization spec2 = createSpecialization("Tim Mạch", "Chuyên điều trị các bệnh lý về tim và mạch máu.");
        Specialization spec3 = createSpecialization("Tiêu Hóa", "Chuyên khám và điều trị bệnh lý đường tiêu hóa.");

        // 4. Tạo Phòng khám
        Clinic clinic1 = createClinic("Bệnh viện Hữu nghị Việt Đức", "40 Tràng Thi, Hoàn Kiếm, Hà Nội");
        Clinic clinic2 = createClinic("Bệnh viện Chợ Rẫy", "201B Nguyễn Chí Thanh, Phường 12, Quận 5, TP. HCM");

        // 5. Liên kết Bác sĩ với Chuyên khoa và Phòng khám (tạo DoctorUser)
        createDoctorInfo(doctor1, clinic2, spec2); // BS Hùng - Chợ Rẫy - Tim Mạch
        createDoctorInfo(doctor2, clinic1, spec1); // BS Lan - Việt Đức - Cơ Xương Khớp

        // 6. Tạo Lịch làm việc cho Bác sĩ
        // BS Hùng có 2 lịch
        Schedule schedule1 = createSchedule(doctor1, LocalDate.now().plusDays(1), "T1", 10); // Sáng mai
        Schedule schedule2 = createSchedule(doctor1, LocalDate.now().plusDays(2), "T2", 15); // Chiều ngày kia
        // BS Lan có 1 lịch
        Schedule schedule3 = createSchedule(doctor2, LocalDate.now().plusDays(1), "T1", 12); // Sáng mai

        // 7. Tạo một vài Lịch hẹn mẫu
        createBooking(patient1, doctor1, schedule1, "Khám định kỳ");
        createBooking(patient2, doctor1, schedule1, "Đau ngực"); // 2 người đặt vào cùng 1 lịch

        log.info("Data initialization complete.");
    }

    // Helper methods to keep the main method clean
    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private User createUser(String name, String email, String password, String address, String phone, String gender, Role role) {
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAddress(address);
        user.setPhone(phone);
        user.setGender(gender);
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    private Specialization createSpecialization(String name, String description) {
        Specialization spec = new Specialization();
        spec.setName(name);
        spec.setDescription(description);
        // spec.setImage("url_to_image");
        return specializationRepository.save(spec);
    }

    private Clinic createClinic(String name, String address) {
        Clinic clinic = new Clinic();
        clinic.setName(name);
        clinic.setAddress(address);
        // clinic.setImage("url_to_image");
        return clinicRepository.save(clinic);
    }

    private DoctorUser createDoctorInfo(User doctor, Clinic clinic, Specialization spec) {
        DoctorUser doctorInfo = new DoctorUser();
        doctorInfo.setDoctor(doctor);
        doctorInfo.setClinic(clinic);
        doctorInfo.setSpecialization(spec);
        return doctorUserRepository.save(doctorInfo);
    }

    private Schedule createSchedule(User doctor, LocalDate date, String timeType, int maxBooking) {
        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);
        schedule.setDate(date.toString());
        schedule.setTime(timeType);
        schedule.setMaxBooking(String.valueOf(maxBooking));
        schedule.setSumBooking("0");
        return scheduleRepository.save(schedule);
    }

    private Comment createBooking(User patient, User doctor, Schedule schedule, String reason) {
        // Cập nhật sumBooking
        int currentBookings = Integer.parseInt(schedule.getSumBooking());
        schedule.setSumBooking(String.valueOf(currentBookings + 1));
        scheduleRepository.save(schedule);

        // Tạo booking
        Comment booking = new Comment();
        booking.setPatient(patient);
        booking.setDoctor(doctor);
        booking.setDateBooking(schedule.getDate());
        booking.setTimeBooking(schedule.getTime());
        booking.setStatus("CONFIRMED"); // Giả sử các booking mẫu này đã được xác nhận
        booking.setContent(reason);
        return commentRepository.save(booking);
    }
}