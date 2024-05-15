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
    private Boolean freeBreakfast;

    @Column(name = "pick_up_drop_off", nullable = false)
    private Boolean pickUpDropOff;

    @Column(nullable = false)
    private Boolean restaurant;

    @Column(nullable = false)
    private Boolean bar;

    @Column(nullable = false)
    private Boolean pool;

    @Column(name = "free_internet", nullable = false)
    private Boolean freeInternet;

    @Column(name = "reception_24h", nullable = false)
    private Boolean reception24h;

    @Column(nullable = false)
    private Boolean laundry;

    @ManyToMany(mappedBy = "conveniences")
    private Set<Hotel> hotel;


}
