package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "type_room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean luxury;

    @Column(name = "single_bed", nullable = false)
    private Boolean singleBedroom;

    @Column(name = "twin_bedroom", nullable = false)
    private Boolean twinBedroom;

    @Column(name = "double_bedroom", nullable = false)
    private Boolean doubleBedroom;

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

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private Set<RoomType> roomTypes;
}
