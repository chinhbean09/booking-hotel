package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
}
