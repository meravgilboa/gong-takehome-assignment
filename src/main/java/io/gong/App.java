package io.gong;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

/**
 * Main application for the Calendar Service.
 */
public class App {

    public static void main(String[] args) {
        System.out.println("=== Calendar Service ===");

        if (args.length < 1) {
            displayUsageInstructions();
            return;
        }

        String csvFilePath = args[0];
        System.out.println("Loading calendar data from: " + csvFilePath);

        try {
            CalendarService calendarService = new CalendarService();
            calendarService.loadEvents(csvFilePath);

            // Example: Find available slots for all people with 30-minute duration
            List<String> allPeople = calendarService.getAvailablePeople();
            List<LocalTime> availableSlots = calendarService.findAvailableSlots(
                    allPeople, Duration.ofMinutes(30));

            System.out.println("\nAvailable 30-minute meeting slots for all " +
                    allPeople.size() + " people:");
            if (availableSlots.isEmpty()) {
                System.out.println("No available slots found");
            } else {
                availableSlots.forEach(time -> System.out.println("- " + time));
            }
        } catch (IOException e) {
            System.err.println("Error loading calendar data: " + e.getMessage());
        }
    }

    private static void displayUsageInstructions() {
        System.out.println("This application helps find available meeting slots for multiple people.");
        System.out.println("\nUsage:");
        System.out.println("  mvn compile exec:java -Dexec.args=\"path/to/csv/file\"");
        System.out.println("\nExample:");
        System.out.println("  mvn compile exec:java -Dexec.args=\"src/main/resources/calendar.csv\"");
        System.out.println("\nAlternatively, you can run tests with:");
        System.out.println("  mvn test");
    }
}