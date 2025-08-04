package org.example.doctorcare.public_api.booking;

public final class BookingStatus {
    private BookingStatus() {} // Ngăn không cho khởi tạo lớp tiện ích

    public static final String NEW = "NEW";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED_BY_DOCTOR = "CANCELLED_BY_DOCTOR";
    public static final String CANCELLED_BY_PATIENT = "CANCELLED_BY_PATIENT";
}