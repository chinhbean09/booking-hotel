package com.chinhbean.bookinghotel.services.oauth2;

import com.chinhbean.bookinghotel.entities.User;
import jakarta.transaction.Transactional;

public interface IOAuth2Service {
    @Transactional
    User processGoogleUser(String email, String name, String googleId);
}
