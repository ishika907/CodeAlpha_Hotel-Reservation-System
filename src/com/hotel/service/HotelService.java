package com.hotel.service;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.model.Room;
import com.hotel.model.RoomCategory;
import com.hotel.storage.DataStore;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles room search, booking, and cancellation logic.
 */
public class HotelService {
    private final DataStore dataStore;
    private List<Room> rooms;
    private List<Reservation> reservations;
    private Map<String, List<String>> availability;

    public HotelService(DataStore dataStore) {
        this.dataStore = dataStore;
        loadData();
    }

    private void loadData() {
        rooms = dataStore.loadRooms();
        reservations = dataStore.loadReservations();
        availability = dataStore.loadAvailability();
    }

    private void saveData() {
        dataStore.saveRooms(rooms);
        dataStore.saveReservations(reservations);
        dataStore.saveAvailability(availability);
    }

    public List<Room> getAllRooms() {
        List<Room> sortedRooms = new ArrayList<>(rooms);
        sortedRooms.sort(Comparator.comparingInt(Room::getRoomNumber));
        return sortedRooms;
    }

    public Room getRoomById(String roomId) {
        for (Room room : rooms) {
            if (room.getRoomId().equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    public List<Room> searchRooms(RoomCategory category, Double maxPrice,
                                  Integer minCapacity, LocalDate checkIn, LocalDate checkOut) {
        List<Room> results = new ArrayList<>();

        for (Room room : getAllRooms()) {
            if (category != null && room.getCategory() != category) {
                continue;
            }
            if (maxPrice != null && room.getPricePerNight() > maxPrice) {
                continue;
            }
            if (minCapacity != null && room.getCapacity() < minCapacity) {
                continue;
            }
            if (checkIn != null && checkOut != null) {
                if (!isRoomAvailable(room.getRoomId(), checkIn, checkOut)) {
                    continue;
                }
            }
            results.add(room);
        }

        return results;
    }

    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            return false;
        }

        LocalDate date = checkIn;
        while (date.isBefore(checkOut)) {
            List<String> bookedRooms = availability.get(date.toString());
            if (bookedRooms != null && bookedRooms.contains(roomId)) {
                return false;
            }
            date = date.plusDays(1);
        }
        return true;
    }

    public int calculateNights(LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        return (int) nights;
    }

    public double calculateTotal(Room room, LocalDate checkIn, LocalDate checkOut) {
        int nights = calculateNights(checkIn, checkOut);
        return nights * room.getPricePerNight();
    }

    public Reservation createReservation(String roomId, Guest guest, LocalDate checkIn,
                                         LocalDate checkOut, String paymentId,
                                         String reservationId) {
        Room room = getRoomById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }
        if (!isRoomAvailable(roomId, checkIn, checkOut)) {
            throw new IllegalArgumentException("Room is not available for selected dates.");
        }

        int nights = calculateNights(checkIn, checkOut);
        double total = nights * room.getPricePerNight();
        String id = reservationId != null ? reservationId : generateReservationId();

        Reservation reservation = new Reservation(
                id, roomId, guest, checkIn, checkOut, nights, total, paymentId
        );

        reservations.add(reservation);
        markUnavailable(roomId, checkIn, checkOut);
        saveData();
        return reservation;
    }

    public Reservation cancelReservation(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found.");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Reservation is already cancelled.");
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalArgumentException("Completed reservations cannot be cancelled.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        markAvailable(reservation.getRoomId(), reservation.getCheckIn(), reservation.getCheckOut());
        saveData();
        return reservation;
    }

    public Reservation getReservationById(String reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationId().equalsIgnoreCase(reservationId)) {
                return reservation;
            }
        }
        return null;
    }

    public List<Reservation> getReservationsByEmail(String email) {
        List<Reservation> results = new ArrayList<>();
        String target = email.trim().toLowerCase();
        for (Reservation reservation : reservations) {
            if (reservation.getGuest().getEmail().toLowerCase().equals(target)) {
                results.add(reservation);
            }
        }
        return results;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> sorted = new ArrayList<>(reservations);
        sorted.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return sorted;
    }

    public void initializeDefaultRooms() {
        if (!rooms.isEmpty()) {
            return;
        }

        rooms.add(new Room("RM-101", 101, RoomCategory.STANDARD, 89.99, 2,
                Arrays.asList("Wi-Fi", "TV", "Air Conditioning"), "Cozy standard room."));
        rooms.add(new Room("RM-102", 102, RoomCategory.STANDARD, 89.99, 2,
                Arrays.asList("Wi-Fi", "TV", "Air Conditioning"), "Comfortable standard room."));
        rooms.add(new Room("RM-103", 103, RoomCategory.STANDARD, 94.99, 3,
                Arrays.asList("Wi-Fi", "TV", "Mini Fridge"), "Standard room with extra space."));
        rooms.add(new Room("RM-201", 201, RoomCategory.DELUXE, 149.99, 2,
                Arrays.asList("Wi-Fi", "Smart TV", "Mini Bar", "Balcony"), "Deluxe room with balcony."));
        rooms.add(new Room("RM-202", 202, RoomCategory.DELUXE, 159.99, 3,
                Arrays.asList("Wi-Fi", "Smart TV", "Mini Bar"), "Spacious deluxe room."));
        rooms.add(new Room("RM-203", 203, RoomCategory.DELUXE, 169.99, 2,
                Arrays.asList("Wi-Fi", "Smart TV", "Ocean View"), "Premium deluxe room."));
        rooms.add(new Room("RM-301", 301, RoomCategory.SUITE, 249.99, 4,
                Arrays.asList("Wi-Fi", "Living Area", "Jacuzzi"), "Executive suite."));
        rooms.add(new Room("RM-302", 302, RoomCategory.SUITE, 299.99, 4,
                Arrays.asList("Wi-Fi", "Kitchenette", "Jacuzzi"), "Luxury suite."));
        rooms.add(new Room("RM-303", 303, RoomCategory.SUITE, 349.99, 6,
                Arrays.asList("Wi-Fi", "2 Bedrooms", "Panoramic View"), "Presidential suite."));

        saveData();
    }

    private void markUnavailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        LocalDate date = checkIn;
        while (date.isBefore(checkOut)) {
            String key = date.toString();
            List<String> booked = availability.get(key);
            if (booked == null) {
                booked = new ArrayList<>();
                availability.put(key, booked);
            }
            if (!booked.contains(roomId)) {
                booked.add(roomId);
            }
            date = date.plusDays(1);
        }
    }

    private void markAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        LocalDate date = checkIn;
        while (date.isBefore(checkOut)) {
            String key = date.toString();
            List<String> booked = availability.get(key);
            if (booked != null) {
                booked.remove(roomId);
                if (booked.isEmpty()) {
                    availability.remove(key);
                }
            }
            date = date.plusDays(1);
        }
    }

    private String generateReservationId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
