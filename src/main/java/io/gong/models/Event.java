package io.gong.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Builder
@Data
public class Event {

    private String subject;
    private LocalTime startTime;
    private LocalTime endTime;
}
