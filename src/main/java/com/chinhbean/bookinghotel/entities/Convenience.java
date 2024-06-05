package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "conveniences")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Convenience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "free_breakfast", nullable = false)
    private Boolean freeBreakfast = false;

    @Column(name = "pick_up_drop_off", nullable = false)
    private Boolean pickUpDropOff = false;

    @Column(nullable = false)
    private Boolean restaurant = false;

    @Column(nullable = false)
    private Boolean bar = false;

    @Column(nullable = false)
    private Boolean pool = false;

    @Column(name = "free_internet", nullable = false)
    private Boolean freeInternet = false;

    @Column(name = "reception_24h", nullable = false)
    private Boolean reception24h = false;

    @Column(nullable = false)
    private Boolean laundry = false;

    @ManyToMany(mappedBy = "conveniences", cascade = CascadeType.ALL)
    private Set<Hotel> hotel;
}
