# Gong.io Calendar Availability Service

## Overview
The Calendar Availability Service is a Java application that helps find available meeting slots for a group of people based on their existing calendar events. Given a list of participants and a desired meeting duration, the service identifies all possible time slots during a workday when everyone is available.


### Implementation Approach
I implemented a Boolean Timeline approach that represents the workday (07:00-19:00) as a 720-minute timeline. For each person, a boolean array marks busy minutes as true. The system then performs logical operations across all participants' timelines to find common available slots.


<img width="496" alt="צילום מסך 2025-05-10 ב-23 33 02" src="https://github.com/user-attachments/assets/9ead26f1-8019-4189-a1b6-b1fee9fee81b" />


#### The service:
Loads events from a CSV file into PersonCalendar objects
Converts each person's events into a boolean array (true = busy, false = available)
Performs logical AND operations across all participants' arrays
Identifies consecutive available slots of the requested duration

### Prerequisites
- Java 16 or higher
- Maven 3.6+
-Internet connection (for downloading dependencies)
- IDE with Lombok support recommended (IntelliJ IDEA with annotation processing enabled)

### Building the Project
From the project root directory:

`mvn clean install`

This will:
- Clean previous builds
- Compile source code
- Run annotation processing (Lombok)
- Execute tests
- Package the application

### Running the Application
To run the application with a CSV file:
'mvn compile exec:java -Dexec.args="/path/to/calendar.csv"'

Example:

`mvn compile exec:java -Dexec.args="/Users/meravgilboa/Desktop/gong-takehome-assignment/src/main/resources/io/gong/calendar.csv"`

The application will:

1. Load calendar events from the provided CSV file
2. Find all available 30-minute meeting slots for all people in the file
3. Display the results (start times of available slots)


### Running Tests
To run the test suite:

`mvn test`

This will execute all test cases covering:
- CSV parsing
- Event loading
- Availability calculation with various meeting durations
- Edge cases and boundary conditions
- Different participant combinations

### Method Comparison: Interval Merge vs. Boolean Timeline

| Criterion | Interval Merge (Sweep Line) | Boolean Timeline (Implemented) |
|-----------|------------------------------|--------------------------------|
| **Approach** | Sort and merge each person's busy intervals | Represent 07:00-19:00 as 720-minute boolean arrays |
| **Cognitive Load** | Higher: sorting, merging, intersection logic | Lower: array marking and bitwise AND |
| **Implementation** | More complex with careful edge handling | Simpler with fewer edge cases |
| **Feature Extensibility** | Harder to add constraints | Easy to inject rules via array masks |
| **Granularity** | Arbitrary (minutes, seconds) | Fixed to resolution (minutes) |
| **Memory Use** | O(n·m) (events-centric) | O(n·720) (per-person arrays) |
| **Time Complexity** | O(n·m·log m + n·m) | O(n·m + n·T + T) where T=720 |

#### Why Boolean Timeline?
I chose the Boolean Timeline approach for:
1. Simplicity: The implementation is straightforward with minimal edge cases
2. Performance: Linear time complexity for queries with very low constant factors
3. Maintainability: Code is easy to understand, test, and extend
4. Practicality: For the given problem scope (single day, minute granularity), the fixed memory usage is acceptable

While the Interval Merge approach might be more memory-efficient for sparse calendars with few events, the Boolean Timeline approach provides simpler code, easier reasoning about availability, and better support for additional business rules like preferred meeting times or blocked periods.

### Project Structure

<img width="500" alt="צילום מסך 2025-05-10 ב-23 34 02" src="https://github.com/user-attachments/assets/4887166e-4bb3-4e70-b59a-065ba2408976" />
