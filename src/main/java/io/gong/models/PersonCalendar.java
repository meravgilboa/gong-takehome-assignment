package io.gong.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PersonCalendar {

    private String personName;
    private List<Event> events;

    public PersonCalendar(String personName) {
        this.personName = personName;
        this.events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        events.add(event);
    }
}
