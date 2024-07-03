package com.chinhbean.bookinghotel.controllers;

import com.chinhbean.bookinghotel.entities.ServicePackage;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.services.pack.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
public class ServicePackageController {
    private final PackageService packageService;

    @GetMapping("/get-all-package")
    public ResponseEntity<ResponseObject> getAllPackages() {
        try {
            List<ServicePackage> packages = packageService.getAllPackages();
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(packages)
                            .message("Packages retrieved successfully")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Failed to retrieve packages: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getPackageById(@PathVariable Long id) {
        try {
            ServicePackage servicePackage = packageService.getPackageById(id);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(servicePackage)
                            .message("Package retrieved successfully")
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Failed to retrieve package: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/create-package")
    public ResponseEntity<ResponseObject> createPackage(@Valid @RequestBody ServicePackage servicePackage, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Invalid package creation request")
                                .build());
            }

            ServicePackage createdPackage = packageService.createPackage(servicePackage);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(createdPackage)
                            .message("Package created successfully")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Failed to create package: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updatePackage(@PathVariable Long id, @Valid @RequestBody ServicePackage servicePackage, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Invalid package update request")
                                .build());
            }

            ServicePackage updatedPackage = packageService.updatePackage(id, servicePackage);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(updatedPackage)
                            .message("Package updated successfully")
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Failed to update package: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deletePackage(@PathVariable Long id) {
        try {
            packageService.deletePackage(id);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Package deleted successfully")
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Failed to delete package: " + e.getMessage())
                            .build());
        }
    }
}
