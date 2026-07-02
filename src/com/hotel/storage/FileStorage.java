package com.hotel.storage;

import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Saves and loads data using plain text files.
 * Each line in a file represents one record.
 */
public class FileStorage implements DataStore {
    private final File roomsFile;
    private final File bookingsFile;
    private final File paymentsFile;
    private final File availabilityFile;

    public FileStorage(String dataFolderPath) {
        File dataFolder = new File(dataFolderPath);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.roomsFile = new File(dataFolder, "rooms.txt");
        this.bookingsFile = new File(dataFolder, "bookings.txt");
        this.paymentsFile = new File(dataFolder, "payments.txt");
        this.availabilityFile = new File(dataFolder, "availability.txt");
    }

    @Override
    public List<Room> loadRooms() {
        return loadLines(roomsFile, Room::fromFileLine);
    }

    @Override
    public void saveRooms(List<Room> rooms) {
        saveLines(roomsFile, rooms, Room::toFileLine);
    }

    @Override
    public List<Reservation> loadReservations() {
        return loadLines(bookingsFile, Reservation::fromFileLine);
    }

    @Override
    public void saveReservations(List<Reservation> reservations) {
        saveLines(bookingsFile, reservations, Reservation::toFileLine);
    }

    @Override
    public List<Payment> loadPayments() {
        return loadLines(paymentsFile, Payment::fromFileLine);
    }

    @Override
    public void savePayments(List<Payment> payments) {
        saveLines(paymentsFile, payments, Payment::toFileLine);
    }

    @Override
    public Map<String, List<String>> loadAvailability() {
        Map<String, List<String>> availability = new HashMap<>();
        if (!availabilityFile.exists()) {
            return availability;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(availabilityFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) {
                    continue;
                }
                String date = parts[0];
                List<String> roomIds = new ArrayList<>();
                if (!parts[1].isEmpty()) {
                    for (String roomId : parts[1].split(";")) {
                        roomIds.add(roomId);
                    }
                }
                availability.put(date, roomIds);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load availability file.", e);
        }

        return availability;
    }

    @Override
    public void saveAvailability(Map<String, List<String>> availability) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(availabilityFile))) {
            for (Map.Entry<String, List<String>> entry : availability.entrySet()) {
                String roomIds = String.join(";", entry.getValue());
                writer.write(entry.getKey() + "|" + roomIds);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save availability file.", e);
        }
    }

    private <T> List<T> loadLines(File file, LineParser<T> parser) {
        List<T> items = new ArrayList<>();
        if (!file.exists()) {
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    items.add(parser.parse(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getName(), e);
        }

        return items;
    }

    private <T> void saveLines(File file, List<T> items, LineFormatter<T> formatter) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (T item : items) {
                writer.write(formatter.format(item));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + file.getName(), e);
        }
    }

    @FunctionalInterface
    private interface LineParser<T> {
        T parse(String line);
    }

    @FunctionalInterface
    private interface LineFormatter<T> {
        String format(T item);
    }
}
