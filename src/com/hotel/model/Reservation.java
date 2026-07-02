package com.hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a room booking.
 */
public class Reservation {
    private String reservationId;
    private String roomId;
    private Guest guest;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int nights;
    private double totalAmount;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private String paymentId;

    public Reservation() {
        this.status = ReservationStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    public Reservation(String reservationId, String roomId, Guest guest,
                       LocalDate checkIn, LocalDate checkOut, int nights,
                       double totalAmount, String paymentId) {
        this.reservationId = reservationId;
        this.roomId = roomId;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.totalAmount = totalAmount;
        this.status = ReservationStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
        this.paymentId = paymentId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isActive() {
        return status == ReservationStatus.CONFIRMED;
    }

    public String toFileLine() {
        return reservationId + "|" + roomId + "|" + guest.toFileLine() + "|"
                + checkIn + "|" + checkOut + "|" + nights + "|" + totalAmount + "|"
                + status.name() + "|" + createdAt + "|" + (paymentId == null ? "" : paymentId);
    }

    public static Reservation fromFileLine(String line) {
        String[] parts = line.split("\\|", 12);
        if (parts.length != 12) {
            throw new IllegalArgumentException("Invalid reservation data: " + line);
        }

        Guest guest = new Guest(parts[2], parts[3], parts[4]);
        Reservation reservation = new Reservation();
        reservation.setReservationId(parts[0]);
        reservation.setRoomId(parts[1]);
        reservation.setGuest(guest);
        reservation.setCheckIn(LocalDate.parse(parts[5]));
        reservation.setCheckOut(LocalDate.parse(parts[6]));
        reservation.setNights(Integer.parseInt(parts[7]));
        reservation.setTotalAmount(Double.parseDouble(parts[8]));
        reservation.setStatus(ReservationStatus.fromString(parts[9]));
        reservation.setCreatedAt(LocalDateTime.parse(parts[10]));
        reservation.setPaymentId(parts[11].isEmpty() ? null : parts[11]);
        return reservation;
    }
}
