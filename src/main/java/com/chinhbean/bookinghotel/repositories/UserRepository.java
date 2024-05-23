package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phone);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("UPDATE User u SET u.avatar = ?2 WHERE u.id = ?1")
    @Transactional
    void updateAvatar(long id, MultipartFile avatar);



}
