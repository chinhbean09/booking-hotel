package com.chinhbean.bookinghotel.controllers;

import com.chinhbean.bookinghotel.dtos.PaymentDTO;
import com.chinhbean.bookinghotel.responses.payment.PaymentResponse;
import com.chinhbean.bookinghotel.services.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public PaymentResponse<PaymentDTO.VNPayResponse> pay(
            @RequestParam String bookingId,
            @RequestParam String amount,
            @RequestParam(required = false) String bankCode,
            @RequestParam(required = false) String phoneGuest,
            @RequestParam(required = false) String nameGuest,
            @RequestParam(required = false) String emailGuest,
            HttpServletRequest request) {
        request.setAttribute("bookingId", bookingId);
        request.setAttribute("amount", amount);
        request.setAttribute("bankCode", bankCode);
        request.setAttribute("phoneGuest", phoneGuest);
        request.setAttribute("nameGuest", nameGuest);
        request.setAttribute("emailGuest", emailGuest);
        return new PaymentResponse<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        String bookingId = request.getParameter("vnp_TxnRef");

        if ("00".equals(status)) {
            paymentService.updatePaymentTransactionStatus(bookingId, true);
            response.sendRedirect("http://localhost:3000/payment-return/success");
        } else {
            paymentService.updatePaymentTransactionStatus(bookingId, false);
            response.sendRedirect("http://localhost:3000/payment-return/failed");
        }
    }
}
