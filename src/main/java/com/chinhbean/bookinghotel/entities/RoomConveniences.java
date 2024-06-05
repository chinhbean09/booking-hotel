package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_conveniences")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomConveniences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "convenience_id", nullable = false)
    private RoomConvenience roomConvenience;
}
