package com.chinhbean.bookinghotel.services.dashboard;

import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.chinhbean.bookinghotel.enums.PackageStatus;
import com.chinhbean.bookinghotel.repositories.IBookingRepository;
import com.chinhbean.bookinghotel.repositories.IPaymentTransactionRepository;
import com.chinhbean.bookinghotel.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashboardService {

    private final IBookingRepository bookingRepository;

    private final IUserRepository userRepository;

    private final IPaymentTransactionRepository paymentTransactionRepository;

    @Override
    public BigDecimal getTotalRevenueFromPaidBookings() {
        return bookingRepository.findTotalRevenueByStatus(BookingStatus.PAID);
    }

    @Override
    public BigDecimal getTotalRevenueFromActivePackages() {
        return userRepository.findTotalRevenueByPackageStatus(PackageStatus.ACTIVE);
    }

    public Map<Long, BigDecimal> getTotalRevenueByPackage() {
        List<Object[]> results = paymentTransactionRepository.findTotalRevenueByPackage();
        Map<Long, BigDecimal> revenueByPackage = new HashMap<>();
        for (Object[] result : results) {
            Long packageId = (Long) result[0];
            Double totalRevenueDouble = (Double) result[1];
            BigDecimal totalRevenue = BigDecimal.valueOf(totalRevenueDouble);
            revenueByPackage.put(packageId, totalRevenue);
        }
        return revenueByPackage;
    }

}
