package com.chinhbean.bookinghotel.enums;

public enum BookingStatus {
    PENDING, CONFIRMED, PAID, CHECKED_IN, CHECKED_OUT, CANCELLED
    //PENDING: Đặt phòng đã được tạo nhưng chưa được xác nhận hoặc thanh toán.
    //CONFIRMED: Đặt phòng đã được xác nhận bởi nhà cung cấp dịch vụ.
    //PAID: Đặt phòng đã được thanh toán.
    //CHECKED_IN: Khách hàng đã nhận phòng.
    //CHECKED_OUT: Khách hàng đã trả phòng.
    //CANCELLED: Đặt phòng đã bị hủy bởi khách hàng hoặc nhà cung cấp dịch vụ.
}
