package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "type")
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

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private Set<RoomType> roomTypes;

    // Setter to ensure that only select one type bedroom
    public void setSingleBedroom(Boolean singleBedroom) {
        if (singleBedroom != null && singleBedroom) {
            this.twinBedroom = false;
            this.doubleBedroom = false;
        }
        this.singleBedroom = singleBedroom;
    }

    public void setTwinBedroom(Boolean twinBedroom) {
        if (twinBedroom != null && twinBedroom) {
            this.singleBedroom = false;
            this.doubleBedroom = false;
        }
        this.twinBedroom = twinBedroom;
    }

    public void setDoubleBedroom(Boolean doubleBedroom) {
        if (doubleBedroom != null && doubleBedroom) {
            this.singleBedroom = false;
            this.twinBedroom = false;
        }
        this.doubleBedroom = doubleBedroom;
    }
}
