package io.gong.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private String subject;
    private LocalTime startTime;
    private LocalTime endTime;
}
