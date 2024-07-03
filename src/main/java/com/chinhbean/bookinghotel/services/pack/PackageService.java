package com.chinhbean.bookinghotel.services.pack;

import com.chinhbean.bookinghotel.entities.ServicePackage;
import com.chinhbean.bookinghotel.repositories.ServicePackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageService implements IPackageService {

    private final ServicePackageRepository servicePackageRepository;

    public List<ServicePackage> getAllPackages() {
        return servicePackageRepository.findAll();
    }

    public ServicePackage getPackageById(Long id) {
        return servicePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package with ID: " + id + " does not exist."));
    }

    public ServicePackage createPackage(ServicePackage servicePackage) {
        validatePackage(servicePackage);
        return servicePackageRepository.save(servicePackage);
    }

    public ServicePackage updatePackage(Long id, ServicePackage updatedPackage) {
        ServicePackage existingPackage = getPackageById(id);
        existingPackage.setName(updatedPackage.getName());
        existingPackage.setDescription(updatedPackage.getDescription());
        existingPackage.setPrice(updatedPackage.getPrice());
        existingPackage.setDurationInMonths(updatedPackage.getDurationInMonths());
        return servicePackageRepository.save(existingPackage);
    }

    public void deletePackage(Long id) {
        ServicePackage existingPackage = getPackageById(id);
        servicePackageRepository.delete(existingPackage);
    }

    private void validatePackage(ServicePackage servicePackage) {
        if (servicePackage.getName() == null || servicePackage.getName().isEmpty()) {
            throw new IllegalArgumentException("Package name cannot be empty");
        }

        if (servicePackage.getPrice() == null || servicePackage.getPrice() <= 0) {
            throw new IllegalArgumentException("Package price must be greater than 0");
        }

        if (servicePackage.getDurationInMonths() == null || servicePackage.getDurationInMonths() <= 0) {
            throw new IllegalArgumentException("Package duration must be greater than 0");
        }

        if (servicePackage.getRegistrationDate() == null) {
            throw new IllegalArgumentException("Package registration date cannot be null");
        }

        if (servicePackage.getExpirationDate() == null) {
            throw new IllegalArgumentException("Package expiration date cannot be null");
        }

        if (servicePackage.getExpirationDate().isBefore(servicePackage.getRegistrationDate())) {
            throw new IllegalArgumentException("Package expiration date cannot be before registration date");
        }

        if (servicePackage.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Package expiration date cannot be in the past");
        }

        if (servicePackage.getDurationInMonths() > 12) {
            throw new IllegalArgumentException("Package duration cannot exceed 12 months");
        }

        if (servicePackage.getDurationInMonths() < 1) {
            throw new IllegalArgumentException("Package duration must be at least 1 month");
        }

        if (servicePackage.getRegistrationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Package registration date cannot be in the past");
        }

        if (servicePackage.getRegistrationDate().isAfter(servicePackage.getExpirationDate())) {
            throw new IllegalArgumentException("Package registration date cannot be after expiration date");
        }

        if (servicePackage.getRegistrationDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Package registration date cannot be in the future");
        }

        if (servicePackage.getDurationInMonths() < 1) {
            throw new IllegalArgumentException("Package duration must be at least 1 month");
        }

    }
}
