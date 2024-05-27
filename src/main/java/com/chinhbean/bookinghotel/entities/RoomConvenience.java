package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "convenience_room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomConvenience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "wardrobe", nullable = false)
    private Boolean wardrobe;
    @Column(name = "air_conditioning", nullable = false)
    private Boolean airConditioning;
    @Column(name = "tv", nullable = false)
    private Boolean tv;
    @Column(name = "wifi", nullable = false)
    private Boolean wifi;
    @Column(name = "toiletries", nullable = false)
    private Boolean toiletries;
    @Column(name = "kitchen", nullable = false)
    private Boolean kitchen;

    @ManyToMany(mappedBy = "roomConveniences")
    private Set<Room> rooms;

}