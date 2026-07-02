package com.hotel.model;

/**
 * Enum representing room types in the hotel.
 */
public enum RoomCategory {
    STANDARD("Standard"),
    DELUXE("Deluxe"),
    SUITE("Suite");

    private final String displayName;

    RoomCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoomCategory fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Room category cannot be empty.");
        }
        String normalized = value.trim().toUpperCase();
        for (RoomCategory category : values()) {
            if (category.name().equals(normalized)
                    || category.displayName.equalsIgnoreCase(value.trim())) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown room category: " + value);
    }
}
