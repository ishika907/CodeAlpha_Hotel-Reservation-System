package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Represents a simulated payment transaction.
 */
public class Payment {
    private String paymentId;
    private String reservationId;
    private double amount;
    private String method;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime processedAt;
    private String cardLastFour;

    public Payment() {
        this.status = PaymentStatus.PENDING;
    }

    public Payment(String paymentId, String reservationId, double amount, String method) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public String toFileLine() {
        return paymentId + "|" + reservationId + "|" + amount + "|" + method + "|"
                + status.name() + "|" + (transactionId == null ? "" : transactionId) + "|"
                + (processedAt == null ? "" : processedAt) + "|"
                + (cardLastFour == null ? "" : cardLastFour);
    }

    public static Payment fromFileLine(String line) {
        String[] parts = line.split("\\|", 8);
        if (parts.length != 8) {
            throw new IllegalArgumentException("Invalid payment data: " + line);
        }

        Payment payment = new Payment();
        payment.setPaymentId(parts[0]);
        payment.setReservationId(parts[1]);
        payment.setAmount(Double.parseDouble(parts[2]));
        payment.setMethod(parts[3]);
        payment.setStatus(PaymentStatus.fromString(parts[4]));
        payment.setTransactionId(parts[5].isEmpty() ? null : parts[5]);
        payment.setProcessedAt(parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6]));
        payment.setCardLastFour(parts[7].isEmpty() ? null : parts[7]);
        return payment;
    }
}
