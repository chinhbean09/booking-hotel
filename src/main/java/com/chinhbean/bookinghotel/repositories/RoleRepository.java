package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<User, Long> {

}
