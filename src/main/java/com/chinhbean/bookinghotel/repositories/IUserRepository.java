package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phone);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("UPDATE User u SET u.avatar = ?2 WHERE u.id = ?1")
    @Transactional
    void updateAvatar(long id, MultipartFile avatar);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR u.fullName ILIKE %:keyword%)")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.id = ?1")
    List<User> findByRoleId(Long roleId);

}
