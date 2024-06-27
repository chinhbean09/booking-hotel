package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {

    @Query("SELECT fp FROM ForgotPassword fp " +
            "WHERE fp.user.email = :email and fp.verified = false " +
            "AND fp.expirationTime = " +
            "( SELECT MAX(fp2.expirationTime) " +
            "FROM ForgotPassword fp2 WHERE fp2.user.email = :email AND fp2.verified = false )")
    Optional<ForgotPassword> findLatestOtpSent(@Param("email") String email);
}
