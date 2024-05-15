package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotel_conveniences")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class HotelConvenience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "convenience_id")
    private Convenience convenience;

}
