package com.hotel.service;

import com.hotel.model.Payment;
import com.hotel.model.PaymentStatus;
import com.hotel.storage.DataStore;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Simulates payment processing (no real payment gateway).
 */
public class PaymentService {
    public static final List<String> PAYMENT_METHODS = Arrays.asList(
            "Credit Card", "Debit Card", "UPI", "Net Banking"
    );

    private final DataStore dataStore;
    private List<Payment> payments;
    private final Random random = new Random();

    public PaymentService(DataStore dataStore) {
        this.dataStore = dataStore;
        this.payments = dataStore.loadPayments();
    }

    private void savePayments() {
        dataStore.savePayments(payments);
    }

    public Payment processPayment(String reservationId, double amount, String method,
                                  String cardLastFour, boolean simulateFailure) {
        if (!PAYMENT_METHODS.contains(method)) {
            throw new IllegalArgumentException("Unsupported payment method.");
        }

        Payment payment = new Payment(generatePaymentId(), reservationId, amount, method);
        payment.setCardLastFour(cardLastFour == null || cardLastFour.isEmpty() ? null : cardLastFour);
        payment.setProcessedAt(LocalDateTime.now());

        boolean success = !simulateFailure && random.nextDouble() > 0.05;
        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionId(null);
        }

        payments.add(payment);
        savePayments();
        return payment;
    }

    public Payment refundPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found.");
        }
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalArgumentException("Only successful payments can be refunded.");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProcessedAt(LocalDateTime.now());
        savePayments();
        return payment;
    }

    public Payment getPaymentById(String paymentId) {
        for (Payment payment : payments) {
            if (payment.getPaymentId().equalsIgnoreCase(paymentId)) {
                return payment;
            }
        }
        return null;
    }

    public Payment getPaymentByReservation(String reservationId) {
        Payment latest = null;
        for (Payment payment : payments) {
            if (payment.getReservationId().equalsIgnoreCase(reservationId)) {
                latest = payment;
            }
        }
        return latest;
    }

    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
