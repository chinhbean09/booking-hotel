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
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @ManyToOne
    @JoinColumn(name = "convenience_id", nullable = false)
    private RoomConvenience roomConvenience;
}
