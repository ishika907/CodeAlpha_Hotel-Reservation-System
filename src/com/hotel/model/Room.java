package com.hotel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a hotel room.
 */
public class Room {
    private String roomId;
    private int roomNumber;
    private RoomCategory category;
    private double pricePerNight;
    private int capacity;
    private List<String> amenities;
    private String description;

    public Room() {
        this.amenities = new ArrayList<>();
    }

    public Room(String roomId, int roomNumber, RoomCategory category,
                double pricePerNight, int capacity, List<String> amenities, String description) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.amenities = new ArrayList<>(amenities);
        this.description = description;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public void setCategory(RoomCategory category) {
        this.category = category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return "Room #" + roomNumber + " (" + category.getDisplayName() + ") | $"
                + String.format("%.2f", pricePerNight) + "/night | Capacity: " + capacity;
    }

    public String toFileLine() {
        String amenityText = String.join(";", amenities);
        return roomId + "|" + roomNumber + "|" + category.name() + "|"
                + pricePerNight + "|" + capacity + "|" + amenityText + "|" + description;
    }

    public static Room fromFileLine(String line) {
        String[] parts = line.split("\\|", 7);
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid room data: " + line);
        }

        List<String> amenityList = new ArrayList<>();
        if (!parts[5].isEmpty()) {
            amenityList.addAll(Arrays.asList(parts[5].split(";")));
        }

        return new Room(
                parts[0],
                Integer.parseInt(parts[1]),
                RoomCategory.fromString(parts[2]),
                Double.parseDouble(parts[3]),
                Integer.parseInt(parts[4]),
                amenityList,
                parts[6]
        );
    }
}
