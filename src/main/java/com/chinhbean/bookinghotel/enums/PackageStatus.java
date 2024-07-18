package com.chinhbean.bookinghotel.enums;

public enum PackageStatus {
    //User can register a new package
    ACTIVE,

    //User register a new package failed
    INACTIVE,

    //user just buying a new package and not paid
    PENDING,

    //Package has been paid
    PAID,

    //Package has been cancelled
    CANCELLED,

    //Package has been expired
    EXPIRED
}
