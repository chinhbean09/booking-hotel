package com.chinhbean.bookinghotel.services.oauth2;

import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.responses.user.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IOAuth2Service {
    User processGoogleUser(String email, String name, String googleId);

    LoginResponse handleFacebookLogin(String accessToken, HttpServletRequest request) throws Exception;

    LoginResponse handleGoogleLogin(String token, HttpServletRequest request) throws Exception;

    User processFacebookUser(String email, String name, String facebookId);

}
