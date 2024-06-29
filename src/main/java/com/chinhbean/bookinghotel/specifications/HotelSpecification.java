package com.chinhbean.bookinghotel.specifications;

import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class HotelSpecification {

    public static Specification<Hotel> hasRating(Integer rating) {
        return (root, query, cb) -> cb.equal(root.get("rating"), rating);
    }

    public static Specification<Hotel> hasFreeBreakfast(Boolean freeBreakfast) {
        return (root, query, cb) -> cb.equal(root.get("conveniences").get("freeBreakfast"), freeBreakfast);
    }

    public static Specification<Hotel> hasPickUpDropOff(Boolean pickUpDropOff) {
        return (root, query, cb) -> cb.equal(root.get("conveniences").get("pickUpDropOff"), pickUpDropOff);
    }

    public static Specification<Hotel> hasRestaurant(Boolean restaurant) {
        return (root, query, cb) -> cb.equal(root.get("conveniences").get("restaurant"), restaurant);
    }

    public static Specification<Hotel> hasBar(Boolean bar) {
        return (root, query, cb) -> cb.equal(root.get("conveniences").get("bar"), bar);
    }

    public static Specification<Hotel> hasPool(Boolean pool) {
        return (root, query, cb) -> cb.equal(root.get("conveniences").get("pool"), pool);
    }

    public static Specification<Hotel> hasFreeInternet(Boolean freeInternet) {
        return (root, query, cb) -> cb.equal(root.get("conveniences").get("freeInternet"), freeInternet);
    }

    public static Specification<Hotel> hasReception24h(Boolean reception24h) {
        return (root, query, cb) -> cb.equal(root.get("conveniences").get("reception24h"), reception24h);
    }

    public static Specification<Hotel> hasLaundry(Boolean laundry) {
        return (root, query, cb) -> cb.equal(root.get("conveniences").get("laundry"), laundry);
    }
}
