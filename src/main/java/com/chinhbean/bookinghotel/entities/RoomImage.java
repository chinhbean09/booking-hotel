package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_urls", nullable = false)
    private String imageUrls;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

}
