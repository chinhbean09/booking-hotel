package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "packages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "duration")
    private Integer durationInMonths; // Thay đổi thành Integer để biểu thị số tháng

    @Column(name = "registration_date")
    private LocalDate registrationDate; // Ngày đăng ký gói

    @Transient
    public LocalDate getExpirationDate() {
        if (registrationDate != null && durationInMonths != null) {
            return registrationDate.plusMonths(durationInMonths);
        }
        return null; // Hoặc bạn có thể xử lý trả về một ngày mặc định khác nếu cần
    }

    // Constructors, getters, setters
}
