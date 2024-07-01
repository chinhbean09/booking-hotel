package com.chinhbean.bookinghotel.utils;

public class MailTemplate {
    public final static class SEND_MAIL_SUBJECT {
        public final static String USER_REGISTER = "ĐĂNG KÍ THÀNH CÔNG!";

        public final static String OTP_SEND = "MÃ OTP XÁC THỰC";

        public final static String NEW_PASSWORD = "YOUR NEW PASSWORD";
    }

    public final static class SEND_MAIL_TEMPLATE {
        public final static String USER_REGISTER = "register";

        public final static String OTP_SEND_TEMPLATE = "otp-sent";

        public final static String NEW_PASSWORD = "your-new-password";
    }
}
