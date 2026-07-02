package com.hotel.storage;

import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.util.List;
import java.util.Map;

/**
 * Interface for saving and loading hotel data.
 * Uses simple file I/O (no database, no Hibernate).
 */
public interface DataStore {
    List<Room> loadRooms();

    void saveRooms(List<Room> rooms);

    List<Reservation> loadReservations();

    void saveReservations(List<Reservation> reservations);

    List<Payment> loadPayments();

    void savePayments(List<Payment> payments);

    Map<String, List<String>> loadAvailability();

    void saveAvailability(Map<String, List<String>> availability);
}
