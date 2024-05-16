package com.chinhbean.bookinghotel.repositories;


import com.chinhbean.bookinghotel.entities.Token;
import com.chinhbean.bookinghotel.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(User user);
    Token findByToken(String token);
    Token findByRefreshToken(String token);
    List<Token> findByUserId(Long userId);
}

