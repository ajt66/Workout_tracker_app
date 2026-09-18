package com.workout_tracker_app.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.PersistenceCreator;

import com.workout_tracker_app.Entity.DailyLog.logStatus;
import com.workout_tracker_app.Entity.programLog.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryDTO {
    private Long program_id;
    private String programName;
    private LocalDate date;
    private String day;
    private Tag tag;
    private List<String> name;
    private List<Integer> weights;
    private List<Integer> sets;
    private List<Integer> reps;
    private List<String> duration;
    private List<String> comment;
    private List<logStatus> status;

    public void setLists() {
        this.name = new ArrayList<String>();
        this.weights = new ArrayList<Integer>();
        this.sets = new ArrayList<Integer>();
        this.reps = new ArrayList<Integer>();
        this.duration = new ArrayList<String>();
        this.comment = new ArrayList<String>();
        this.status = new ArrayList<logStatus>();
    }
}
