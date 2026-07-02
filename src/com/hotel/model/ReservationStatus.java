package com.hotel.model;

public enum ReservationStatus {
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReservationStatus fromString(String value) {
        for (ReservationStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)
                    || status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown reservation status: " + value);
    }
}
