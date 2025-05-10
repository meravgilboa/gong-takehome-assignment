package io.gong.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class Event {
    private String subject;
    private LocalTime startTime;
    private LocalTime endTime;
}
