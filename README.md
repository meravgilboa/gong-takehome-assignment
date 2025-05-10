Gong.io Calendar Availability Service

Gong.io Take Home Exercise

This take home exercise is used to determine how you go about solving problems logically, as well as building out simple and clear code.

What is the exercise?

You will be creating a simple calendar with one really cool feature: Given a list of people and a desired duration, find all the time slots in a day in which all persons are available to meet.

The input data is provided to you in a simple comma-separated values file (calendar.csv) and is structured in the following way:

Person name, Event subject, Event start time, Event end time

Goals

Your goal is to design and create a simple Calendar in Java, and implement the following method:

List<LocalTime> findAvailableSlots(List<String> personList, Duration eventDuration);

Requirements:

This calendar represents a single day, so to make things simple - events have only start and end times (no dates).

The work day starts at 07:00 and ends at 19:00. Take that into consideration when finding available time slots.

Don't forget to add tests as well.

Example

Attached is an example calendar file calendar.csv:

Alice,"Morning meeting",08:00,09:30
Alice,"Lunch with Jack",13:00,14:00
Alice,"Yoga",16:00,17:00
Jack,"Morning meeting",08:00,08:50
Jack,"Sales call",09:00,09:40
Jack,"Lunch with Alice",13:00,14:00
Jack,"Yoga",16:00,17:00
Bob,"Morning meeting",08:00,09:30
Bob,"Morning meeting 2",09:30,09:40
Bob,"Q3 review",10:00,11:30
Bob,"Lunch and siesta",13:00,15:00
Bob,"Yoga",16:00,17:00

For this input, and for a meeting of 60 minutes which Alice, Jack & Bob should attend the following output is expected:

Available slot: 07:00
Available slot: 11:30
Available slot: 15:00
Available slot: 17:00

Getting Started

You will need Maven installed to run the commands below.

You will have to run mvn clean install inside the directory to download and install required dependencies.

We have created the application's entry point for you. The entry point file is src/main/java/io/gong/App.java.
To execute the app, you can run mvn compile exec:java

Overview and Design and Design

We use a timeline-based (boolean-array) approach:

Load events via Apache Commons CSV into PersonCalendar objects.

Convert each person’s busy intervals into a 720‑minute boolean array (true = busy, false = free).

Intersect all selected attendees’ arrays (logical AND over false = free).

Scan the combined array for consecutive false spans of length ≥ desired duration; record the start times.

┌─────────────────────────────────────────────────────────────────┐
│                Work Day: 07:00 ─────────── 19:00               │
│0        60        120 ...           660       720 (minutes)   │
│┌───┐┌──────────────┐      ┌───────┐      ┌─────────────┐       │
││   ││ Busy intervals│ free │ busy │      free intervals      │
││   ││   marked in   │      │      │      marked in          │
││...││ boolean array │      │...   │      boolean array       │
│└───┘└──────────────┘      └───────┘      └─────────────┘       │
└─────────────────────────────────────────────────────────────────┘

Prerequisites

JDK 1.8 or higher (Java 8+)

Maven 3.6+

Internet connection (to download Maven dependencies)

Optional:

IDE with Lombok support (e.g. IntelliJ with annotation processing enabled)

Apache Commons CSV (pulled in via Maven)

Building the Project

From the project root:

mvn clean compile

This will:

Clean previous builds (clean)

Compile source code, run Lombok annotation processing (compile)

Running the Application

By default, the entry point is io.gong.App, which expects a CSV file path:

mvn exec:java -Dexec.mainClass=io.gong.App -Dexec.args="path/to/calendar.csv"

It will:

Load events from calendar.csv

Compute available slots for hard‑coded attendees and duration (customize in App.java)

Print each available start time to the console

Running Tests

We use JUnit 5 for unit tests under src/test/java.

To run all tests:

mvn test

This will:

Compile test sources

Execute the suite covering CSV parsing, edge cases, and availability logic

Project Structure

├── pom.xml                # Maven configuration
├── src
│   ├── main
│   │   ├── java/io/gong/  # Application & service classes
│   │   └── resources/     # Optional CSV resources
│   └── test
│       ├── java/io/gong/  # Unit tests
│       └── resources/     # Test CSV files
└── README.md

Future Enhancements

Support for variable work-day hours or time zones

Sub-minute granularity (e.g. seconds) via a different resolution

Dynamic updates: live insertion/removal of events

UI or REST API for interactive querying

Comparison: Methods Compared

Methodology

Interval Merge (Sweep Line)Sort and merge each person’s busy intervals, then intersect across participants to find common free gaps.

Boolean TimelineMap 07:00–19:00 to a 720-element boolean array per person (true = busy). AND arrays to find mutual free minutes.

Trade-offs

Criterion

Interval Merge

Boolean Timeline

Cognitive Load

Higher: sorting, merging, intersection logic

Lower: array marking and bitwise AND

Implementation

More complex, careful edge handling

Simpler, fewer edge cases

Feature Extensibility

Harder to add constraints (e.g. custom breaks)

Easy to inject rules via array masks

Granularity

Arbitrary (minutes, seconds, etc.)

Fixed to resolution (e.g. minutes)

Memory Use

O(n·m) (events-centric)

O(n·720) (per‐person arrays)

Parallelism

Limited

Highly parallelizable (per-person ANDs)

Complexity

Let n = number of people, m = average events/person, T = 720 minutes, d = duration

Interval Merge Time: O(n·m·log m + n·m) merges + intersections

Interval Merge Space: O(n·m) for intervals

Boolean Timeline Time: O(n·m_event + n·T + T) ≈ O(n·m + n·T)

Boolean Timeline Space: O(n·T) for boolean arrays

Why This Approach?

Speed & Simplicity: Linear-time per query (O(n)) with very low constant factors.

Maintainability: Lower cognitive load and easy testability.

Scalability: Handles large numbers of participants and frequent queries efficiently.

This foundation supports dynamic rules, real-time updates, and simple REST/GUI integrations.

