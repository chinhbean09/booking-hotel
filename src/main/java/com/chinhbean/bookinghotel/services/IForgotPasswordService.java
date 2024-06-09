package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;

public interface IForgotPasswordService {

    void verifyEmailAndSendOtp(String email) throws DataNotFoundException;

    void verifyOTP(String email, Integer otp) throws DataNotFoundException;

}
