package io.gong;

import io.gong.model.Event;
import io.gong.model.PersonCalendar;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
/**
 * Service to manage calendar operations including finding available meeting slots
 * for multiple participants.
 */
public class CalendarService {
    private static final LocalTime START_WORK_DAY = LocalTime.of(7, 0);
    private static final LocalTime END_WORK_DAY = LocalTime.of(19, 0);
    private static final int WORK_DAY_MINUTES = (int) Duration.between(START_WORK_DAY, END_WORK_DAY).toMinutes();
    private static final int NUM_OF_CSV_FIELDS = 4;

    // Map of person name to their calendar
    private final Map<String, PersonCalendar> personCalendars = new HashMap<>();

    /**
     * Loads calendar events from a CSV file in the classpath.
     *
     * @param resourcePath Path to the CSV file within the classpath
     * @throws IOException If an I/O error occurs reading from the file
     */
    public void loadEvents(String filePath) throws IOException {
        InputStream inputStream;

        // First try to load as a classpath resource
        inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

        // If not found in classpath, try as a regular file
        if (inputStream == null) {
            try {
                inputStream = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                throw new IOException("File not found: " + filePath);
            }
        }

        try (InputStream is = inputStream) {
            if (is == null) {
                throw new IOException("Resource not found: " + filePath);
            }

            CSVParser parser = CSVFormat.DEFAULT
                    .builder()
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .setQuote('"')
                    .build()
                    .parse(new InputStreamReader(is));

            // Rest of your parsing code remains the same
            for (CSVRecord record : parser) {
                // Existing parsing logic...
                if (record.size() < NUM_OF_CSV_FIELDS) {
                    System.err.println("Skipping invalid record: " + record);
                    continue;
                }

                try {
                    String personName = record.get(0);
                    String subject = record.get(1);
                    LocalTime startTime = LocalTime.parse(record.get(2));
                    LocalTime endTime = LocalTime.parse(record.get(3));

                    Event event = new Event(subject, startTime, endTime);
                    personCalendars.computeIfAbsent(personName, PersonCalendar::new)
                            .addEvent(event);
                } catch (Exception e) {
                    System.err.println("Error processing record: " + record + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * Finds all time slots when all requested people are available for a meeting.
     *
     * @param personList    List of people who need to attend the meeting
     * @param eventDuration Desired duration of the meeting
     * @return List of available start times for the meeting
     */
    public List<LocalTime> findAvailableSlots(List<String> personList, Duration eventDuration) {
        // Filter none-existing people
        personList = personList.stream()
                .filter(personCalendars::containsKey)
                .toList();

        if (personList.isEmpty()) {
            return Collections.emptyList();
        }
        if (eventDuration.isNegative() || eventDuration.isZero()) {
            return Collections.emptyList();
        }
        boolean[][] availability = createAvailabilityMatrix(personList);
        return findAvailableTimeSlots(availability, eventDuration);
    }

    /**
     * Finds time slots when all people are available for the specified duration.
     *
     * @param availability  Matrix of busy times for each person
     * @param eventDuration Desired duration of the meeting
     * @return List of available start times
     */
    private List<LocalTime> findAvailableTimeSlots(boolean[][] availability, Duration eventDuration) {
        int requiredDuration = (int) eventDuration.toMinutes();
        List<LocalTime> availableSlots = new ArrayList<>();
        int  maxStartMinute = WORK_DAY_MINUTES - requiredDuration;

        for (int minute = 0; minute < maxStartMinute; minute++) {
            if (isTimeSlotAvailable(availability, minute, requiredDuration)) {
                availableSlots.add(START_WORK_DAY.plusMinutes(minute));
                minute += requiredDuration - 1; // Skip to the end of the slot
            }
        }
        return availableSlots;
    }

    /**
     * Checks if a specific time slot is available for all people.
     *
     * @param availability Matrix of busy times for each person
     * @param startMinute  Starting minute to check
     * @param duration     Duration to check in minutes
     * @return true if everyone is available, false otherwise
     */
    private boolean isTimeSlotAvailable(boolean[][] availability, int startMinute, int duration) {
        for (boolean[] booleans : availability) {
            for (int minute = 0; minute < duration; minute++) {
                if (booleans[startMinute + minute]) {
                    return false; // Someone is busy during this time
                }
            }
        }
        return true; // Everyone is available for the entire duration
    }

    /**
     * Creates a matrix representing each person's availability during the day.
     * False = available, True = busy
     *
     * @param personList List of people to check availability for
     * @return 2D boolean array where [person][minute] represents availability
     */
    private boolean[][] createAvailabilityMatrix(List<String> personList) {
        boolean[][] availability = new boolean[personList.size()][WORK_DAY_MINUTES];

        for (int i = 0; i < personList.size(); i++) {
            String person = personList.get(i);
            markBusyTimesForPerson(availability, i, person);
        }
        return availability;
    }

    /**
     * Marks all busy times for a specific person in the availability matrix.
     *
     * @param availability Matrix to update
     * @param personIndex  Index of the person in the matrix
     * @param personName   Name of the person
     */
    private void markBusyTimesForPerson(boolean[][] availability, int personIndex, String personName) {
        personCalendars.get(personName).getEvents()
                .forEach(event -> {
                    int startMinute = convertTimeToMinuteOffset(event.getStartTime());
                    int endMinute = convertTimeToMinuteOffset(event.getEndTime());
                    markTimeRangeAsBusy(availability, personIndex, startMinute, endMinute);
                });
    }

    /**
     * Converts a LocalTime to minutes offset from the start of the work day.
     *
     * @param time The time to convert
     * @return Minutes from the start of work day
     */
    private int convertTimeToMinuteOffset(LocalTime time) {
        return (int) Duration.between(START_WORK_DAY, time).toMinutes();
    }

    /**
     * Marks a range of minutes as busy for a specific person.
     *
     * @param availability Matrix to update
     * @param personIndex  Index of the person in the matrix
     * @param startMinute  Start minute (inclusive)
     * @param endMinute    End minute (exclusive)
     */
    private void markTimeRangeAsBusy(boolean[][] availability, int personIndex, int startMinute, int endMinute) {
        for (int minute = startMinute; minute < endMinute; minute++) {
            if (minute >= 0 && minute < WORK_DAY_MINUTES) {
                availability[personIndex][minute] = true;
            }
        }
    }

    public List<String> getAvailablePeople() {
        return personCalendars.keySet().stream().toList();
    }
}