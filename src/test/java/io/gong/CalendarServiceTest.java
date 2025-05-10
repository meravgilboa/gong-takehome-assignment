package io.gong;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalendarServiceTest {

    private CalendarService calendarService;

    @BeforeEach
    public void setup() throws IOException {
        calendarService = new CalendarService();
        calendarService.loadEvents("io/gong/calendar.csv");
    }


    @Test
    public void testFindAvailableSlotsForOneHourMeeting() {
        List<String> people = Arrays.asList("Alice", "Jack", "Bob");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> expectedAvailableSlots = Arrays.asList(
                LocalTime.of(7, 0),
                LocalTime.of(11, 30),
                LocalTime.of(15, 0),
                LocalTime.of(17, 0)
        );

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);
        assertEquals(expectedAvailableSlots, availableSlots);
    }

    @Test
    public void testFindAvailableSlots100MinMeeting() {
        List<String> people = Arrays.asList("Alice", "Jack", "Bob");
        Duration meetingDuration = Duration.ofMinutes(100);

        List<LocalTime> expectedAvailableSlots = Collections.singletonList(
                LocalTime.of(17, 0)
        );

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);
        assertEquals(expectedAvailableSlots, availableSlots);
    }

    @Test
    public void testFindAvailableSlotsSinglePerson() {
        List<String> people = Collections.singletonList("Alice");
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> expectedAvailableSlots = Arrays.asList(
                LocalTime.of(7, 0),
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                LocalTime.of(11, 30),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                LocalTime.of(17, 0)
        );

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);
        assertEquals(expectedAvailableSlots, availableSlots);
    }


    @Test
    public void testFindAvailableSlotsBackToBackEvents() {
        List<String> people = Collections.singletonList("Bob");
        Duration meetingDuration = Duration.ofMinutes(30);

        List<LocalTime> expectedAvailableSlots = Arrays.asList(
                LocalTime.of(7, 0),
                LocalTime.of(7, 30),
                LocalTime.of(11, 30),
                LocalTime.of(12, 0),
                LocalTime.of(12, 30),
                LocalTime.of(15, 0),
                LocalTime.of(15, 30),
                LocalTime.of(17, 0),
                LocalTime.of(17, 30),
                LocalTime.of(18, 0)
        );

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);
        assertEquals(expectedAvailableSlots, availableSlots);
    }

    @Test
    public void testFindAvailableSlotsWithEmptyPersonList() {
        List<String> people = Collections.emptyList();
        Duration meetingDuration = Duration.ofMinutes(60);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);
        assertEquals(Collections.emptyList(), availableSlots);
    }

    @Test
    public void testFindAvailableSlotsForNonExistPerson() {
        List<String> people = Collections.singletonList("NonexistentPerson");
        Duration meetingDuration = Duration.ofMinutes(120);

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);
        assertEquals(Collections.emptyList(), availableSlots);
    }

    @Test
    public void testFindAvailableSlotsWithShortMeetingDuration() {
        List<String> people = Arrays.asList("Alice", "Jack", "Bob");
        Duration meetingDuration = Duration.ofMinutes(15);

        List<LocalTime> expectedAvailableSlots = Arrays.asList(
                LocalTime.of(7, 0),
                LocalTime.of(7, 15),
                LocalTime.of(7, 30),
                LocalTime.of(7, 45),
                LocalTime.of(9, 40),
                LocalTime.of(11, 30),
                LocalTime.of(11, 45),
                LocalTime.of(12, 0),
                LocalTime.of(12, 15),
                LocalTime.of(12, 30),
                LocalTime.of(12, 45),
                LocalTime.of(15, 0),
                LocalTime.of(15, 15),
                LocalTime.of(15, 30),
                LocalTime.of(15, 45),
                LocalTime.of(17, 0),
                LocalTime.of(17, 15),
                LocalTime.of(17, 30),
                LocalTime.of(17, 45),
                LocalTime.of(18, 0),
                LocalTime.of(18, 15),
                LocalTime.of(18, 30)
        );

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);
        assertEquals(expectedAvailableSlots, availableSlots);
    }

    @Test
    public void testFindAvailableSlotsWithExceedingMeetingDuration() {
        List<String> people = Arrays.asList("Alice", "Jack", "Bob");
        Duration meetingDuration = Duration.ofMinutes(300); // 5 hours

        List<LocalTime> availableSlots = calendarService.findAvailableSlots(people, meetingDuration);
        assertEquals(Collections.emptyList(), availableSlots);
    }
}
