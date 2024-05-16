package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phone);

    boolean existsByPhoneNumber(String phoneNumber);

}
