package com.chinhbean.bookinghotel.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HotelConvenienceId implements Serializable {
    private Long hotel;
    private Long convenience;
}
