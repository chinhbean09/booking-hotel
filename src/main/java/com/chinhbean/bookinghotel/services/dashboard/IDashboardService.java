package com.chinhbean.bookinghotel.services.dashboard;

import java.math.BigDecimal;
import java.util.Map;

public interface IDashboardService {


    public BigDecimal getTotalRevenueFromPaidBookings();

    BigDecimal getTotalRevenueFromActivePackages();

    Map<Long, BigDecimal> getTotalRevenueByPackage();


}
