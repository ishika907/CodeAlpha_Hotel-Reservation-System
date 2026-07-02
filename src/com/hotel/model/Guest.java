package com.hotel.model;

/**
 * Represents a hotel guest.
 */
public class Guest {
    private String name;
    private String email;
    private String phone;

    public Guest() {
    }

    public Guest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** Converts guest data to a single line for file storage. */
    public String toFileLine() {
        return name + "|" + email + "|" + phone;
    }

    public static Guest fromFileLine(String line) {
        String[] parts = line.split("\\|", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid guest data: " + line);
        }
        return new Guest(parts[0], parts[1], parts[2]);
    }
}
