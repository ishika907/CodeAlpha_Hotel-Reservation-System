package com.hotel.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Helper methods for console input and output.
 */
public class ConsoleHelper {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ConsoleHelper() {
    }

    public static void printHeader(String title) {
        System.out.println("\n============================================================");
        System.out.println("  " + title);
        System.out.println("============================================================");
    }

    public static void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i]);
        }
        System.out.println("  0. Exit");
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public static String readNonEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    public static int readInt(String prompt, Integer minimum) {
        while (true) {
            String input = readLine(prompt);
            try {
                int value = Integer.parseInt(input);
                if (minimum != null && value < minimum) {
                    System.out.println("Value must be at least " + minimum + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public static double readDouble(String prompt, Double minimum) {
        while (true) {
            String input = readLine(prompt);
            try {
                double value = Double.parseDouble(input);
                if (minimum != null && value < minimum) {
                    System.out.println("Value must be at least " + minimum + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            String input = readLine(prompt + " (YYYY-MM-DD): ");
            try {
                LocalDate date = LocalDate.parse(input);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Date cannot be in the past.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    public static LocalDate[] readDateRange() {
        LocalDate checkIn = readDate("Check-in date");
        while (true) {
            LocalDate checkOut = readDate("Check-out date");
            if (checkOut.isAfter(checkIn)) {
                return new LocalDate[]{checkIn, checkOut};
            }
            System.out.println("Check-out must be after check-in.");
        }
    }

    public static void pause() {
        readLine("\nPress Enter to continue...");
    }

    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMAT);
    }
}
