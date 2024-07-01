package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String roleUser);
}
