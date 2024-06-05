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
@IdClass(HotelConvenienceId.class)
public class HotelConvenience {

    @Id
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Id
    @ManyToOne
    @JoinColumn(name = "convenience_id")
    private Convenience convenience;

}
