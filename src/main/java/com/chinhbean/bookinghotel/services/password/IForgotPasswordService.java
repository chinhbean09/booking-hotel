package com.chinhbean.bookinghotel.services.password;

import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;

public interface IForgotPasswordService {

    void verifyEmailAndSendOtp(String email) throws DataNotFoundException;

    void verifyOTP(String email, Integer otp) throws DataNotFoundException;

}
