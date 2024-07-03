package com.chinhbean.bookinghotel.services.pack;

import com.chinhbean.bookinghotel.entities.ServicePackage;

import java.util.List;

public interface IPackageService {
    List<ServicePackage> getAllPackages();

    ServicePackage getPackageById(Long id);

    ServicePackage createPackage(ServicePackage servicePackage);

    ServicePackage updatePackage(Long id, ServicePackage servicePackage);

    void deletePackage(Long id);
}
