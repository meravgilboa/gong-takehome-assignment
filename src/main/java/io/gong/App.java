package io.gong;

/**
 * Main application for the Calendar Service.
 * This class provides information about running the tests.
 */
public class App {

    public static void main(String[] args) {
        System.out.println("=== Calendar Service ===");
        System.out.println("This application helps find available meeting slots for multiple people.");
        System.out.println();

        System.out.println("=== How to Run the Tests ===");
        System.out.println("To run the CalendarServiceTest suite, use one of the following methods:");
        System.out.println();

        System.out.println("1. Using Maven:");
        System.out.println("   mvn test");
        System.out.println();

        System.out.println("2. Using IntelliJ IDEA:");
        System.out.println("   Right-click on CalendarServiceTest class and select 'Run'");
        System.out.println();

        System.out.println("3. Using Maven with specific test:");
        System.out.println("   mvn test -Dtest=CalendarServiceTest");
        System.out.println();

        System.out.println("Tests will verify various calendar scheduling scenarios including:");
        System.out.println("- Finding available slots for single and multiple participants");
        System.out.println("- Handling various meeting durations");
        System.out.println("- Processing events at work day boundaries");
        System.out.println("- CSV calendar data processing");
    }
}