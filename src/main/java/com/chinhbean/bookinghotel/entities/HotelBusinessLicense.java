package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotel_business_license")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelBusinessLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "business_license_url", nullable = false)
    private String businessLicense;
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
