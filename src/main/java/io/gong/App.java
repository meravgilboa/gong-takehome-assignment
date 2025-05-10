package io.gong;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Calendar Application entry point.
 * Demonstrates the functionality of CalendarService by allowing users
 * to find available meeting slots for selected people.
 */
public class App {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        System.out.println("=== Calendar Meeting Slot Finder ===");

        try {
            // Initialize the calendar service
            CalendarService calendarService = new CalendarService();
            calendarService.loadEvents("io/gong/calendar.csv");
            System.out.println("Calendar data loaded successfully.");

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("\nAvailable commands:");
                System.out.println("1. Find available meeting slots");
                System.out.println("2. Exit");
                System.out.print("Enter your choice: ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        findAvailableSlots(scanner, calendarService);
                        break;
                    case "2":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

            System.out.println("Thank you for using Calendar Meeting Slot Finder!");

        } catch (IOException e) {
            System.err.println("Error loading calendar data: " + e.getMessage());
        }
    }

    private static void findAvailableSlots(Scanner scanner, CalendarService calendarService) {
        System.out.println("\n=== Find Available Meeting Slots ===");

        // Get participants
        System.out.print("Enter participant names (comma-separated, e.g., Alice,Bob): ");
        String peopleInput = scanner.nextLine();
        List<String> people = Arrays.asList(peopleInput.split(","));

        // Get meeting duration
        System.out.print("Enter meeting duration (minutes): ");
        int minutes = Integer.parseInt(scanner.nextLine());
        Duration meetingDuration = Duration.ofMinutes(minutes);

        // Find available slots
        System.out.println("\nSearching for available " + minutes + "-minute slots for: " + String.join(", ", people));
        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Display results
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots found for the requested participants and duration.");
        } else {
            System.out.println("Available slots:");
            for (LocalTime slot : availableSlots) {
                LocalTime endTime = slot.plusMinutes(minutes);
                System.out.println("• " + slot.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER));
            }
            System.out.println("Total available slots: " + availableSlots.size());
        }
    }
}