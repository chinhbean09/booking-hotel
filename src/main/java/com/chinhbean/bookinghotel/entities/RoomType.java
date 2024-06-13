package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

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

    @Column(name = "capacity_per_room", nullable = false)
    private int capacityPerRoom;

    @Column(name = "number_of_room", nullable = false)
    private Integer numberOfRoom;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_type_id")
    private Set<RoomImage> roomImages;

    @NotEmpty(message = "At least one convenience must be selected")
    @ManyToMany
    @JoinTable(
            name = "room_conveniences",
            joinColumns = @JoinColumn(name = "room_type_id"),
            inverseJoinColumns = @JoinColumn(name = "convenience_id")
    )
    private Set<RoomConvenience> roomConveniences;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "status")
    private Integer status;
}