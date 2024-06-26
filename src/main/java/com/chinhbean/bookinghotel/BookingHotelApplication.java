package com.chinhbean.bookinghotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BookingHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingHotelApplication.class, args);
    }

}
