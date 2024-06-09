package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "forgot_passwords")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgotPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "otp", nullable = false)
    private Integer otp;
    @Column(name = "expiration_date", nullable = false)
    private Date expirationTime;
    @Column(name = "verified", nullable = false)
    private Boolean verified;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
