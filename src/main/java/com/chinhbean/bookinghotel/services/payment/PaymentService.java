package com.chinhbean.bookinghotel.services.payment;

import com.chinhbean.bookinghotel.configurations.VNPAYConfig;
import com.chinhbean.bookinghotel.dtos.PaymentDTO;
import com.chinhbean.bookinghotel.entities.Booking;
import com.chinhbean.bookinghotel.entities.BookingDetails;
import com.chinhbean.bookinghotel.entities.PaymentTransaction;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.chinhbean.bookinghotel.enums.PackageStatus;
import com.chinhbean.bookinghotel.repositories.*;
import com.chinhbean.bookinghotel.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final IPaymentTransactionRepository IPaymentTransactionRepository;
    private final IBookingRepository bookingRepository;
    private final IServicePackageRepository IServicePackageRepository;
    private final IUserRepository userRepository;
    private final IBookingDetailRepository bookingDetailRepository;
    private final IRoomTypeRepository roomTypeRepository;


    @Transactional
    public PaymentDTO.VNPayResponse createVnPayPaymentForBooking(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getAttribute("amount").toString()) * 100L;
        String bankCode = (String) request.getAttribute("bankCode");
        Long bookingId = Long.parseLong(request.getAttribute("bookingId").toString());
        String transactionCode;
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID: " + bookingId + " does not exist."));

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(bookingId.toString(), "Thanh toan don hang: " + bookingId);

        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }

        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        queryUrl += "&vnp_SecureHash=" + VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        // Retrieve the vnp_TxnRef
        transactionCode = vnpParamsMap.get("vnp_TxnRef");

        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    @Transactional
    public PaymentDTO.VNPayResponse createVnPayPaymentForPackage(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getAttribute("amount").toString()) * 100L;
        String bankCode = (String) request.getAttribute("bankCode");
        Long packageId = Long.parseLong(request.getAttribute("packageId").toString());
        String userEmail = (String) request.getAttribute("emailGuest");

        IServicePackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package with ID: " + packageId + " does not exist."));

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(packageId.toString(),
                "Thanh toan goi dich vu: " + packageId + " cho user " + userEmail);

        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        // Generate secure hash
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + secureHash;

        // Form the payment URL
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }


    @Transactional
    public void updatePaymentTransactionStatusForBooking(String bookingId, boolean isSuccess) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID: " + bookingId + " does not exist."));

        if (isSuccess) {
            booking.setStatus(BookingStatus.PAID);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            List<BookingDetails> bookingDetails = bookingDetailRepository.findByBookingId(id);
            for (BookingDetails bookingDetail : bookingDetails) {
                roomTypeRepository.incrementRoomQuantity(bookingDetail.getRoomType().getId(), bookingDetail.getNumberOfRooms());
            }
            IPaymentTransactionRepository.deleteByEmailGuest(booking.getEmail());

        }
        bookingRepository.save(booking);
    }

    @Transactional
    public void updatePaymentTransactionStatusForPackage(String email, boolean isSuccess) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {

            throw new IllegalArgumentException("User with email: " + email + " does not exist.");
        }

        if (isSuccess) {

            user.get().setStatus(PackageStatus.ACTIVE);
            userRepository.save(user.get());
        } else {
            user.get().setServicePackage(null);
            user.get().setPackageStartDate(null);
            user.get().setPackageEndDate(null);
            user.get().setStatus(PackageStatus.INACTIVE);
            new PaymentTransaction();
            userRepository.save(user.get());
            IPaymentTransactionRepository.deleteByEmailGuest(email);

        }
    }
}
