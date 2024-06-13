package com.chinhbean.bookinghotel.entities;

import com.chinhbean.bookinghotel.enums.HotelStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_name", nullable = false)
    private String hotelName;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private User partner;

    @Column(name = "business_license")
    private String businessLicense;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HotelStatus status;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "hotel_conveniences",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "convenience_id")
    )
    private Set<Convenience> conveniences;

    @OneToOne(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private HotelLocation location;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoomType> roomTypes;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Feedback> feedbacks;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private Set<HotelImages> hotelImages;

}
