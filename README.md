# Gong.io Take Home Exercise

This take home exercise is used to determine how you go about solving problems logically, as well as building out simple and clear code.  

### What is the exercise?
You will be creating a simple calendar with one really cool feature: Given a list of people and a desired duration, find all the time slots in a day in which all persons are available to meet.

The input data is provided to you in a simple comma-separated values file (`calendar.csv`) and is structured in the following way:

```
Person name, Event subject, Event start time, Event end time
```   

### Goals

Your goal is to design and create a simple Calendar in Java, and implement the following method:

```java
List<LocalTime> findAvailableSlots(List<String> personList, Duration eventDuration);
``` 

#### Requirements:
- This calendar represents a single day, so to make things simple - events have only start and end times (no dates).
- The work day starts at 07:00 and ends at 19:00. Take that into consideration when finding available time slots.
- Don't forget to add tests as well.


### Example
Attached is an example calendar file `calendar.csv`:
```
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
```

For this input, and for a meeting of 60 minutes which Alice, Jack & Bob should attend the following output is expected:

```
Available slot: 07:00
Available slot: 11:30
Available slot: 15:00
Available slot: 17:00
```


### Getting Started

You will need [Maven](https://maven.apache.org/) installed to run the commands below.

You will have to run `mvn clean install` inside the directory to download and install required dependencies.

We have created the application's entry point for you. The entry point file is `src/main/java/io/gong/App.java`.
To execute the app, you can run `mvn compile exec:java`

