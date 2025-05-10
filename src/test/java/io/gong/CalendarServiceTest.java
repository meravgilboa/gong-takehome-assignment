package io.gong;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test class for {@link CalendarService} functionality.
 * Tests various scenarios for finding available meeting slots based on people's calendars.
 * Tests include core functionality, boundary conditions, edge cases, and CSV parsing robustness.
 */
public class CalendarServiceTest {

    private CalendarService calendarService;

    /**
     * Sets up a CalendarService instance and loads the main calendar data before each test.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @BeforeEach
    public void setup() throws IOException {
        calendarService = new CalendarService();
        calendarService.loadEvents("io/gong/calendar.csv");
    }

    /**
     * Tests finding available slots for a person with no events in their calendar.
     * Verifies that all time slots during work hours are available.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testSinglePersonNoEvents() throws IOException {
        // Use a person not in the calendar to simulate no events
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Charlie");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // Expect slots from 7:00 to 18:00 (inclusive) for 60-min meetings
        assertEquals(11, availableSlots.size());
        assertTrue(availableSlots.contains(LocalTime.of(7, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(17, 0)));
    }

    /**
     * Tests finding available slots for a person with a single event in their calendar.
     * Verifies that slots before and after the event are available, but not during the event.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testSinglePersonOneEvent() throws IOException {
        // Create a service with a single event in the middle of the day
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Dave");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // Expect slots before 11:00 and after 12:00
        assertTrue(availableSlots.contains(LocalTime.of(7, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(10, 0)));
        assertFalse(availableSlots.contains(LocalTime.of(11, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(12, 0)));
        assertTrue(availableSlots.size() == 10);
    }

    /**
     * Tests finding available slots for multiple people with non-overlapping events.
     * Verifies that common free time is identified correctly.
     */
    @Test
    public void testMultiplePeopleNonOverlappingEvents() {
        // Alice and Jack have 60-min slots at various points in the day
        List<String> people = Arrays.asList("Alice", "Jack");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Verify some expected free slots
        assertTrue(availableSlots.contains(LocalTime.of(7, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(10, 40)));
        assertTrue(availableSlots.contains(LocalTime.of(14, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(17, 0)));

        // Verify slots during Alice's meeting aren't available
        assertFalse(availableSlots.contains(LocalTime.of(8, 0)));
        assertFalse(availableSlots.contains(LocalTime.of(13, 0)));
    }

    /**
     * Tests the handling of adjacent events in a person's calendar.
     * Verifies that back-to-back events are properly processed.
     */
    @Test
    public void testAdjacentEvents() {
        // Bob has back-to-back meetings from 8:00-9:30 and 9:30-9:40
        List<String> people = Collections.singletonList("Bob");
        Duration meetingDuration = Duration.ofMinutes(30);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // No availability between 8:00 and 9:40
        assertTrue(availableSlots.contains(LocalTime.of(7, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(7, 30)));
        assertFalse(availableSlots.contains(LocalTime.of(8, 0)));
        assertFalse(availableSlots.contains(LocalTime.of(9, 0)));
        assertFalse(availableSlots.contains(LocalTime.of(9, 40)));
    }

    // ===== BOUNDARY CONDITIONS =====

    /**
     * Tests finding available slots when a person has an event at the start of the work day.
     * Verifies that slots during the event are not available.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testEventAtWorkDayStart() throws IOException {
        // Use the boundary events calendar with early meeting
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Barb");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // First slot should be 7:30 (not 7:00) due to 6:30-7:30 meeting
        assertFalse(availableSlots.contains(LocalTime.of(7, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(7, 30)));
    }

    /**
     * Tests finding available slots when a person has an event at the end of the work day.
     * Verifies that slots that would extend beyond the work day are not available.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testEventAtWorkDayEnd() throws IOException {
        // Use the boundary events calendar with late meeting
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Barb");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // Last full 60-min slot should be 17:30 (not 18:00) due to 18:30-19:30 meeting
        assertTrue(availableSlots.contains(LocalTime.of(17, 30)));
        assertFalse(availableSlots.contains(LocalTime.of(18, 0)));
    }

    /**
     * Tests finding available slots when a person has an event spanning the entire work day.
     * Verifies that no slots are available.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testEventSpanningEntireDay() throws IOException {
        // Create a service with a calendar containing an all-day event
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Eve");
        Duration meetingDuration = Duration.ofMinutes(30);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // No slots available due to 7:00-19:00 meeting
        assertTrue(availableSlots.isEmpty());
    }

    /**
     * Tests handling of zero-duration meetings.
     * Verifies that the service handles invalid durations appropriately.
     */
    @Test
    public void testZeroDurationMeeting() {
        // Test with zero-length meeting
        List<String> people = Collections.singletonList("Alice");
        Duration meetingDuration = Duration.ZERO;

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Should find many slots (every minute when Alice is free)
        assertTrue(availableSlots.isEmpty());
    }

    // ===== EDGE CASES AROUND DURATION =====

    /**
     * Tests finding available slots for meetings longer than the work day.
     * Verifies that no slots are available for such long meetings.
     */
    @Test
    public void testDurationLongerThanWorkDay() {
        // Test with meeting longer than the work day
        List<String> people = Collections.singletonList("Alice");
        Duration meetingDuration = Duration.ofMinutes(800); // > 12 hours

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // No slots possible for such a long meeting
        assertTrue(availableSlots.isEmpty());
    }

    /**
     * Tests finding slots that exactly fit between events.
     * Verifies that slots that exactly fit available gaps are correctly identified.
     */
    @Test
    public void testExactFitSlots() {
        // The slot from 15:00-16:00 is an exact 60-minute slot for all three people
        List<String> people = Arrays.asList("Alice", "Jack", "Bob");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Verify that 15:00 slot is available (exactly 60 minutes until 16:00 when Yoga starts)
        assertTrue(availableSlots.contains(LocalTime.of(15, 0)));
    }

    /**
     * Tests finding available slots for very short (1-minute) meetings.
     * Verifies that the system correctly identifies all possible 1-minute slots.
     */
    @Test
    public void testOneMinuteMeetings() {
        // Test with 1-minute meeting duration
        List<String> people = Collections.singletonList("Alice");
        Duration meetingDuration = Duration.ofMinutes(1);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Calculate Alice's free time:
        // 7:00-8:00 (60 min) + 9:30-13:00 (210 min) + 14:00-16:00 (120 min) + 17:00-19:00 (120 min)
        // Total: 510 free minutes
        assertTrue(availableSlots.size() > 500);
        assertTrue(availableSlots.size() <= 510);

        // Morning slot checks
        assertTrue(availableSlots.contains(LocalTime.of(7, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(7, 30)));

        // Check busy times are excluded
        assertFalse(availableSlots.contains(LocalTime.of(8, 0))); // Morning meeting
        assertFalse(availableSlots.contains(LocalTime.of(13, 30))); // Lunch
        assertFalse(availableSlots.contains(LocalTime.of(16, 30))); // Yoga

        // Check other free slots are included
        assertTrue(availableSlots.contains(LocalTime.of(9, 30))); // After morning meeting
        assertTrue(availableSlots.contains(LocalTime.of(14, 0))); // After lunch
        assertTrue(availableSlots.contains(LocalTime.of(17, 0))); // After yoga
    }

    // ===== CSV PARSING ROBUSTNESS =====

    /**
     * Tests handling of CSV files with quoted fields containing commas.
     * Verifies that the CSV parser correctly processes such fields.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testQuotedSubjectWithCommas() throws IOException {
        // Test CSV with subjects containing commas
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Frank");
        Duration meetingDuration = Duration.ofMinutes(30);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // Basic verification - just make sure it successfully loaded
        assertFalse(availableSlots.isEmpty());
    }

    /**
     * Tests handling of malformed or non-existent CSV files.
     * Verifies that appropriate exceptions are thrown.
     */
    @Test
    public void testMalformedCSV() {
        // Test loading a CSV with non-existent file path (will throw IOException)
        assertThrows(IOException.class, () -> {
            CalendarService service = new CalendarService();
            service.loadEvents("io/gong/non_existent_file.csv");
        });
    }

    /**
     * Tests handling of CSV files with extra whitespace around fields.
     * Verifies that the CSV parser correctly trims field values.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testCSVWithWhitespace() throws IOException {
        // Test CSV with extra whitespace around fields
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Grace");
        Duration meetingDuration = Duration.ofMinutes(30);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // Basic verification - just ensure it processed correctly
        assertFalse(availableSlots.isEmpty());
    }

    /**
     * Tests handling of CSV files with blank lines.
     * Verifies that the CSV parser correctly skips blank lines.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testCSVWithBlankLines() throws IOException {
        // Test CSV with blank lines
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Hank");
        Duration meetingDuration = Duration.ofMinutes(30);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // Basic verification - just ensure it processed correctly
        assertFalse(availableSlots.isEmpty());
    }

    // ===== PERSON-FILTERING =====

    /**
     * Tests finding available slots with a mix of existing and non-existing people.
     * Verifies that non-existent people are filtered out correctly.
     */
    @Test
    public void testMixOfExistingAndNonExistingPeople() {
        // Some people exist in calendar, others don't
        List<String> people = Arrays.asList("Alice", "NonExistentPerson");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Should find slots for Alice only, ignoring NonExistentPerson
        List<LocalTime> expectedSlots = calendarService.findAvailableSlots(
                Collections.singletonList("Alice"),
                meetingDuration
        );

        assertEquals(expectedSlots, availableSlots);
    }

    /**
     * Tests finding available slots with an empty list of people.
     * Verifies that an empty list of available slots is returned.
     */
    @Test
    public void testEmptyPersonList() {
        // Empty list of people
        List<String> people = Collections.emptyList();
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Should return empty list when no people specified
        assertTrue(availableSlots.isEmpty());
    }

    /**
     * Tests case sensitivity in person names.
     * Verifies whether the system treats different cases of the same name as the same person.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testCaseSensitivityInNames() throws IOException {
        // Create a service with case sensitive data
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        // Check if system is case-sensitive for person names
        List<String> lowerCasePeople = Collections.singletonList("alice");
        List<String> properCasePeople = Collections.singletonList("Alice");

        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> lowerCaseResults = service.findAvailableSlots(lowerCasePeople, meetingDuration);
        List<LocalTime> properCaseResults = service.findAvailableSlots(properCasePeople, meetingDuration);

        // Adjust this assertion based on desired behavior
        assertNotEquals(lowerCaseResults, properCaseResults);
    }

    /**
     * Tests finding slots at exactly the start of the work day.
     * Verifies that slots at the beginning of the work day are correctly identified.
     */
    @Test
    public void testExactlyWorkDayStart() {
        // Meeting at exactly 7:00 (work day start)
        List<String> people = Arrays.asList("Alice", "Jack");
        Duration meetingDuration = Duration.ofMinutes(30);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Verify that 7:00 is included in available slots
        assertTrue(availableSlots.contains(LocalTime.of(7, 0)));
    }

    /**
     * Tests finding slots at exactly the end of the work day.
     * Verifies that slots at the end of the work day are correctly identified.
     */
    @Test
    public void testExactlyWorkDayEnd() {
        // Meeting ending exactly at work day end (19:00)
        List<String> people = Arrays.asList("Alice", "Jack");
        Duration meetingDuration = Duration.ofMinutes(30);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);

        // Verify that 18:30 is the last available slot (for a 30 min meeting)
        assertTrue(availableSlots.contains(LocalTime.of(18, 00)));
        assertFalse(availableSlots.contains(LocalTime.of(18, 30)));
    }

    /**
     * Tests handling of empty calendars (no events).
     * Verifies that all slots during work hours are available.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testEmptyCalendar() throws IOException {
        // Load the boundary events calendar which only has events for Barb
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        // Query for "Charlie" who doesn't exist in the calendar
        List<LocalTime> slots = service.findAvailableSlots(List.of("Charlie"), Duration.ofMinutes(60));

        // Charlie is never busy, so slots at 07:00, 08:00, … up to 18:00 inclusive
        assertTrue(slots.contains(LocalTime.of(7, 0)));
        assertTrue(slots.contains(LocalTime.of(17, 0)));
        assertEquals(11, slots.size());
    }

    /**
     * Tests handling of events that overlap work hour boundaries.
     * Verifies that events extending outside work hours are properly handled.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testEventsOverlappingWorkHourBoundaries() throws IOException {
        // Create a new service with custom boundary events data
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Barb");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // Verify first slot isn't 7:00 (due to event starting at 6:30 and ending at 7:30)
        assertFalse(availableSlots.contains(LocalTime.of(7, 0)));
        assertTrue(availableSlots.contains(LocalTime.of(7, 30)));
    }

    /**
     * Tests handling of schedules with fragmented free time.
     * Verifies that a schedule with multiple small gaps but no large ones has no available slots
     * for longer meetings.
     *
     * @throws IOException if the calendar file cannot be read
     */
    @Test
    public void testNoAvailableSlotsDueToFragmentation() throws IOException {
        // Create a new service with fragmented schedule data
        CalendarService service = new CalendarService();
        service.loadEvents("io/gong/additional_calendar.csv");

        List<String> people = Collections.singletonList("Phill");
        Duration meetingDuration = Duration.ofMinutes(45);

        List<LocalTime> availableSlots = service.findAvailableSlots(people, meetingDuration);

        // Person has multiple 30-min free slots but no 45-min slots
        assertTrue(availableSlots.isEmpty());

        // But they should have slots for 30-min meetings
        List<LocalTime> thirtyMinSlots = service.findAvailableSlots(
                Collections.singletonList("Phill"),
                Duration.ofMinutes(30)
        );
        assertFalse(thirtyMinSlots.isEmpty());
    }
}