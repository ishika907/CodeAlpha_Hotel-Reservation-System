package com.hotel;

import com.hotel.model.Guest;
import com.hotel.model.Payment;
import com.hotel.model.PaymentStatus;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.RoomCategory;
import com.hotel.service.HotelService;
import com.hotel.service.PaymentService;
import com.hotel.util.ConsoleHelper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Console menu for the Hotel Reservation System.
 */
public class HotelApp {
    private final HotelService hotelService;
    private final PaymentService paymentService;

    public HotelApp(HotelService hotelService, PaymentService paymentService) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
    }

    public void start() {
        hotelService.initializeDefaultRooms();
        ConsoleHelper.printHeader("Welcome to Grand Horizon Hotel Reservation System");

        while (true) {
            ConsoleHelper.printMenu(new String[]{
                    "Search Available Rooms",
                    "Book a Room",
                    "View Booking Details",
                    "Cancel Reservation",
                    "View All Rooms",
                    "Manage Reservations (Admin)"
            });

            String choice = ConsoleHelper.readLine("Enter your choice: ");

            try {
                switch (choice) {
                    case "1":
                        searchRooms();
                        break;
                    case "2":
                        bookRoom();
                        break;
                    case "3":
                        viewBookingDetails();
                        break;
                    case "4":
                        cancelReservation();
                        break;
                    case "5":
                        viewAllRooms();
                        break;
                    case "6":
                        manageReservations();
                        break;
                    case "0":
                        System.out.println("\nThank you for using Grand Horizon Hotel. Goodbye!");
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please try again.");
                        ConsoleHelper.pause();
                }
            } catch (IllegalArgumentException e) {
                System.out.println("\nError: " + e.getMessage());
                ConsoleHelper.pause();
            }
        }
    }

    private void searchRooms() {
        ConsoleHelper.printHeader("Search Available Rooms");
        System.out.println("\nFilter options (leave blank to skip):");

        String categoryInput = ConsoleHelper.readLine("Category (Standard/Deluxe/Suite): ");
        RoomCategory category = null;
        if (!categoryInput.isEmpty()) {
            category = RoomCategory.fromString(categoryInput);
        }

        String maxPriceInput = ConsoleHelper.readLine("Maximum price per night: ");
        Double maxPrice = maxPriceInput.isEmpty() ? null : Double.parseDouble(maxPriceInput);

        String capacityInput = ConsoleHelper.readLine("Minimum capacity: ");
        Integer minCapacity = capacityInput.isEmpty() ? null : Integer.parseInt(capacityInput);

        LocalDate checkIn = null;
        LocalDate checkOut = null;
        if (ConsoleHelper.readLine("Filter by dates? (y/n): ").equalsIgnoreCase("y")) {
            LocalDate[] dates = ConsoleHelper.readDateRange();
            checkIn = dates[0];
            checkOut = dates[1];
        }

        List<Room> rooms = hotelService.searchRooms(category, maxPrice, minCapacity, checkIn, checkOut);

        if (rooms.isEmpty()) {
            System.out.println("\nNo rooms match your search criteria.");
        } else {
            System.out.println("\nFound " + rooms.size() + " room(s):\n");
            for (Room room : rooms) {
                System.out.println("  [" + room.getRoomId() + "] " + room.getSummary());
                System.out.println("      " + room.getDescription());
            }
        }
        ConsoleHelper.pause();
    }

    private void viewAllRooms() {
        ConsoleHelper.printHeader("All Hotel Rooms");
        RoomCategory currentCategory = null;

        for (Room room : hotelService.getAllRooms()) {
            if (room.getCategory() != currentCategory) {
                currentCategory = room.getCategory();
                System.out.println("\n--- " + currentCategory.getDisplayName() + " Rooms ---");
            }
            System.out.println("  [" + room.getRoomId() + "] " + room.getSummary());
        }
        ConsoleHelper.pause();
    }

    private void bookRoom() {
        ConsoleHelper.printHeader("Book a Room");
        LocalDate[] dates = ConsoleHelper.readDateRange();
        LocalDate checkIn = dates[0];
        LocalDate checkOut = dates[1];

        List<Room> available = hotelService.searchRooms(null, null, null, checkIn, checkOut);
        if (available.isEmpty()) {
            System.out.println("\nNo rooms are available for the selected dates.");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("\nAvailable rooms:\n");
        for (int i = 0; i < available.size(); i++) {
            Room room = available.get(i);
            int nights = hotelService.calculateNights(checkIn, checkOut);
            double total = hotelService.calculateTotal(room, checkIn, checkOut);
            System.out.println("  " + (i + 1) + ". [" + room.getRoomId() + "] "
                    + room.getCategory().getDisplayName() + " Room #" + room.getRoomNumber());
            System.out.println("     " + ConsoleHelper.formatCurrency(room.getPricePerNight())
                    + "/night | Total (" + nights + " nights): " + ConsoleHelper.formatCurrency(total));
        }

        int selection = ConsoleHelper.readInt("\nSelect room number: ", 1);
        if (selection > available.size()) {
            throw new IllegalArgumentException("Invalid room selection.");
        }

        Room selectedRoom = available.get(selection - 1);
        double totalAmount = hotelService.calculateTotal(selectedRoom, checkIn, checkOut);

        ConsoleHelper.printHeader("Guest Information");
        Guest guest = new Guest(
                ConsoleHelper.readNonEmpty("Full name: "),
                ConsoleHelper.readNonEmpty("Email: "),
                ConsoleHelper.readNonEmpty("Phone: ")
        );

        ConsoleHelper.printHeader("Booking Summary");
        System.out.println("  Room: #" + selectedRoom.getRoomNumber()
                + " (" + selectedRoom.getCategory().getDisplayName() + ")");
        System.out.println("  Check-in: " + checkIn);
        System.out.println("  Check-out: " + checkOut);
        System.out.println("  Total: " + ConsoleHelper.formatCurrency(totalAmount));

        if (!ConsoleHelper.readLine("\nProceed to payment? (y/n): ").equalsIgnoreCase("y")) {
            System.out.println("\nBooking cancelled.");
            ConsoleHelper.pause();
            return;
        }

        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment = collectPayment(reservationId, totalAmount);

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            System.out.println("\nPayment failed. Booking was not created.");
            ConsoleHelper.pause();
            return;
        }

        Reservation reservation = hotelService.createReservation(
                selectedRoom.getRoomId(), guest, checkIn, checkOut,
                payment.getPaymentId(), reservationId
        );

        ConsoleHelper.printHeader("Booking Confirmed");
        displayBookingDetails(reservation, selectedRoom, payment);
        ConsoleHelper.pause();
    }

    private Payment collectPayment(String reservationId, double amount) {
        ConsoleHelper.printHeader("Payment Simulation");
        System.out.println("  Amount due: " + ConsoleHelper.formatCurrency(amount));
        System.out.println("\nPayment methods:");

        for (int i = 0; i < PaymentService.PAYMENT_METHODS.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + PaymentService.PAYMENT_METHODS.get(i));
        }

        int methodChoice = ConsoleHelper.readInt("Select payment method: ", 1);
        if (methodChoice > PaymentService.PAYMENT_METHODS.size()) {
            throw new IllegalArgumentException("Invalid payment method.");
        }

        String method = PaymentService.PAYMENT_METHODS.get(methodChoice - 1);
        String cardLastFour = "";

        if (method.equals("Credit Card") || method.equals("Debit Card")) {
            cardLastFour = ConsoleHelper.readNonEmpty("Enter last 4 digits of card: ");
        }

        boolean simulateFailure = ConsoleHelper.readLine("Simulate payment failure? (y/n): ")
                .equalsIgnoreCase("y");

        System.out.println("\nProcessing payment...");
        return paymentService.processPayment(reservationId, amount, method, cardLastFour, simulateFailure);
    }

    private void viewBookingDetails() {
        ConsoleHelper.printHeader("View Booking Details");
        String lookup = ConsoleHelper.readLine("Search by (1) Reservation ID or (2) Email: ");

        Reservation reservation = null;

        if (lookup.equals("1")) {
            String reservationId = ConsoleHelper.readNonEmpty("Reservation ID: ");
            reservation = hotelService.getReservationById(reservationId);
        } else if (lookup.equals("2")) {
            String email = ConsoleHelper.readNonEmpty("Email: ");
            List<Reservation> reservations = hotelService.getReservationsByEmail(email);

            if (reservations.isEmpty()) {
                System.out.println("\nNo bookings found for this email.");
                ConsoleHelper.pause();
                return;
            }

            if (reservations.size() == 1) {
                reservation = reservations.get(0);
            } else {
                System.out.println("\nMultiple bookings found:\n");
                for (int i = 0; i < reservations.size(); i++) {
                    Reservation item = reservations.get(i);
                    Room room = hotelService.getRoomById(item.getRoomId());
                    String roomLabel = room != null ? "Room #" + room.getRoomNumber() : item.getRoomId();
                    System.out.println("  " + (i + 1) + ". " + item.getReservationId() + " | "
                            + roomLabel + " | " + item.getCheckIn() + " to " + item.getCheckOut()
                            + " | " + item.getStatus().getDisplayName());
                }
                int selection = ConsoleHelper.readInt("Select booking: ", 1);
                if (selection > reservations.size()) {
                    throw new IllegalArgumentException("Invalid selection.");
                }
                reservation = reservations.get(selection - 1);
            }
        } else {
            throw new IllegalArgumentException("Invalid lookup option.");
        }

        if (reservation == null) {
            System.out.println("\nBooking not found.");
            ConsoleHelper.pause();
            return;
        }

        Room room = hotelService.getRoomById(reservation.getRoomId());
        Payment payment = reservation.getPaymentId() != null
                ? paymentService.getPaymentById(reservation.getPaymentId())
                : null;

        displayBookingDetails(reservation, room, payment);
        ConsoleHelper.pause();
    }

    private void displayBookingDetails(Reservation reservation, Room room, Payment payment) {
        System.out.println("\n--- Booking Details ---");
        System.out.println("  Reservation ID : " + reservation.getReservationId());
        System.out.println("  Status         : " + reservation.getStatus().getDisplayName());
        System.out.println("  Created        : " + ConsoleHelper.formatDateTime(reservation.getCreatedAt()));
        System.out.println("  Guest Name     : " + reservation.getGuest().getName());
        System.out.println("  Guest Email    : " + reservation.getGuest().getEmail());
        System.out.println("  Guest Phone    : " + reservation.getGuest().getPhone());

        if (room != null) {
            System.out.println("  Room           : #" + room.getRoomNumber()
                    + " (" + room.getCategory().getDisplayName() + ")");
            System.out.println("  Rate           : "
                    + ConsoleHelper.formatCurrency(room.getPricePerNight()) + "/night");
        } else {
            System.out.println("  Room ID        : " + reservation.getRoomId());
        }

        System.out.println("  Check-in       : " + reservation.getCheckIn());
        System.out.println("  Check-out      : " + reservation.getCheckOut());
        System.out.println("  Nights         : " + reservation.getNights());
        System.out.println("  Total Amount   : " + ConsoleHelper.formatCurrency(reservation.getTotalAmount()));

        if (payment != null) {
            System.out.println("\n--- Payment Details ---");
            System.out.println("  Payment ID     : " + payment.getPaymentId());
            System.out.println("  Method         : " + payment.getMethod());
            System.out.println("  Status         : " + payment.getStatus().getDisplayName());
            System.out.println("  Amount         : " + ConsoleHelper.formatCurrency(payment.getAmount()));
            if (payment.getTransactionId() != null) {
                System.out.println("  Transaction ID : " + payment.getTransactionId());
            }
            if (payment.getCardLastFour() != null && !payment.getCardLastFour().isEmpty()) {
                System.out.println("  Card           : **** **** **** " + payment.getCardLastFour());
            }
            if (payment.getProcessedAt() != null) {
                System.out.println("  Processed At   : " + ConsoleHelper.formatDateTime(payment.getProcessedAt()));
            }
        }
    }

    private void cancelReservation() {
        ConsoleHelper.printHeader("Cancel Reservation");
        String reservationId = ConsoleHelper.readNonEmpty("Reservation ID: ");
        Reservation reservation = hotelService.getReservationById(reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found.");
        }
        if (reservation.getStatus().getDisplayName().equals("Cancelled")) {
            throw new IllegalArgumentException("Reservation is already cancelled.");
        }

        Room room = hotelService.getRoomById(reservation.getRoomId());
        System.out.println("\nReservation to cancel:");
        displayBookingDetails(reservation, room, null);

        if (!ConsoleHelper.readLine("\nConfirm cancellation? (y/n): ").equalsIgnoreCase("y")) {
            System.out.println("\nCancellation aborted.");
            ConsoleHelper.pause();
            return;
        }

        hotelService.cancelReservation(reservationId);
        Payment payment = paymentService.getPaymentByReservation(reservationId);

        if (payment != null && payment.getStatus() == PaymentStatus.SUCCESS) {
            paymentService.refundPayment(payment.getPaymentId());
            System.out.println("\nPayment refund processed successfully.");
        }

        System.out.println("\nReservation " + reservationId.toUpperCase() + " has been cancelled.");
        ConsoleHelper.pause();
    }

    private void manageReservations() {
        ConsoleHelper.printHeader("Manage Reservations");
        List<Reservation> reservations = hotelService.getAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("\nNo reservations on record.");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("\nTotal reservations: " + reservations.size() + "\n");
        for (Reservation reservation : reservations) {
            Room room = hotelService.getRoomById(reservation.getRoomId());
            String roomLabel = room != null ? "#" + room.getRoomNumber() : reservation.getRoomId();
            System.out.println("  " + reservation.getReservationId() + " | "
                    + reservation.getGuest().getName() + " | Room " + roomLabel + " | "
                    + reservation.getCheckIn() + " to " + reservation.getCheckOut() + " | "
                    + reservation.getStatus().getDisplayName() + " | "
                    + ConsoleHelper.formatCurrency(reservation.getTotalAmount()));
        }
        ConsoleHelper.pause();
    }
}
