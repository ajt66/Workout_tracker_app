package com.workout_tracker_app.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryDTO {
    private LocalDate date;
    private String day;
    private String tag;
    private List<DLDTO> logs;

    public void setValues() {
        this.date = logs.get(0).getLogging_date();
        this.day = logs.get(0).getDay();
        this.tag = logs.get(0).getProgram_tag();
    }
}
