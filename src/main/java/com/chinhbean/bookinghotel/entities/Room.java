//package com.chinhbean.bookinghotel.entities;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.DecimalMin;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.util.Set;
//
//@Entity
//@Table(name = "rooms")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class Room {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "hotel_id", nullable = false)
//    private Hotel hotel;
//
//    @NotBlank(message = "Room number is required")
//    @Column(name = "room_number", nullable = false)
//    private String roomNumber;
//
//    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
//    @NotNull(message = "Price is required")
//    @Column(nullable = false)
//    private BigDecimal price;
//
//    @NotBlank(message = "Availability status is required")
//    @Column(nullable = false)
//    private String availability;
//    @OneToMany
//    @JoinColumn(name = "room_id")
//    private Set<RoomImage> roomImages;
//
//    @NotEmpty(message = "At least one room type must be selected")
//    @ManyToMany
//    @JoinTable(
//            name = "room_type",
//            joinColumns = @JoinColumn(name = "room_id"),
//            inverseJoinColumns = @JoinColumn(name = "type_id")
//    )
//    private Set<Type> types;
//
//    @NotEmpty(message = "At least one convenience must be selected")
//    @ManyToMany
//    @JoinTable(
//            name = "room_conveniences",
//            joinColumns = @JoinColumn(name = "room_id"),
//            inverseJoinColumns = @JoinColumn(name = "convenience_id")
//    )
//    private Set<RoomConvenience> roomConveniences;
//
//}
