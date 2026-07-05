Hotel Reservation System

The Hotel Reservation System is a Java-based console application developed using Object-Oriented Programming (OOP) principles. The system allows users to search available rooms, make reservations, cancel bookings, simulate payments, and view booking details.

Features
Search rooms — by category, price, capacity, dates
Book a room — guest info + payment simulation
View booking details — by reservation ID or email
Cancel reservation — frees dates + auto refund
View all rooms — grouped by Standard / Deluxe / Suite
Admin view — list all reservations

'''text
Hotel Reservation System/
├── compile.bat              # Compile all Java files
├── run.bat                  # Run the application
├── src/com/hotel/
│   ├── Main.java            # Entry point
│   ├── HotelApp.java        # Console menu & user interaction
│   ├── model/               # OOP classes & enums
│   │   ├── Room.java
│   │   ├── RoomCategory.java    (enum: Standard, Deluxe, Suite)
│   │   ├── Guest.java
│   │   ├── Reservation.java
│   │   ├── ReservationStatus.java
│   │   ├── Payment.java
│   │   └── PaymentStatus.java
│   ├── service/             # Business logic
│   │   ├── HotelService.java
│   │   └── PaymentService.java
│   ├── storage/             # File I/O persistence
│   │   ├── DataStore.java       (interface)
│   │   └── FileStorage.java     (text files)
│   └── util/
│       └── ConsoleHelper.java
└── data/                    # Auto-created on first run
    ├── rooms.txt
    ├── bookings.txt
    ├── payments.txt
    └── availability.txt
'''

    Java Concepts Used
Concept	Where
Classes & Objects
Room, Guest, Reservation, Payment
Encapsulation
Private fields with getters/setters
Enums
RoomCategory, ReservationStatus, PaymentStatus
Interface
DataStore implemented by FileStorage
Collections
ArrayList, HashMap, List, Map
File I/O
BufferedReader, BufferedWriter, FileReader, FileWriter
Exception Handling
Input validation, IllegalArgumentException
Java 8 Time API
LocalDate, LocalDateTime

 How to Run

1. Clone the repository.
2. Open the project in IntelliJ IDEA, VS Code.
3. Make sure Java JDK is installed.
4. Run `Main.java`.

============================================================
  Welcome to Grand Horizon Hotel Reservation System
============================================================
  1. Search Available Rooms
  2. Book a Room
  3. View Booking Details
  4. Cancel Reservation
  5. View All Rooms
  6. Manage Reservations (Admin)
  0. Exit
Enter your choice:

Future Enhancements
MySQL Database Integration
User Login & Authentication
Admin Dashboard
Check-in / Check-out Dates
Online Payment Gateway
Email Notifications

Author
Your Name
Ishika Jain
