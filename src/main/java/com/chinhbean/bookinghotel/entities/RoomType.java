package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;

    @Column(name = "room_price", nullable = false)
    private Double roomPrice;

    @Column(name = "number_of_room", nullable = false)
    private Integer numberOfRoom;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_urls", nullable = false)
    private String imageUrls;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "status")
    private int status;

}
