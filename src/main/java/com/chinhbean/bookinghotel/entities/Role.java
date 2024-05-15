package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    public static String ADMIN = "ADMIN";
    public static String PARTNER = "PARTNER";
    public static String CUSTOMER = "CUSTOMER";
}
