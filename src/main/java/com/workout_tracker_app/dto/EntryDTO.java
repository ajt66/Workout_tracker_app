package com.workout_tracker_app.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryDTO {
    private String date;
    private String day;
    private String tag;
    private List<DLDTO> logs = new ArrayList<DLDTO>(5);
    private List<DLDTO> newLogs = new ArrayList<DLDTO>(5);

    public void setValues() {
        this.date = this.logs.get(0).getLogging_date().toString();
        this.day = this.logs.get(0).getDay();
        this.tag = this.logs.get(0).getProgram_tag();
    }

    public void setLogs(List<DLDTO> logs) {
        this.logs = logs;
    }

}
