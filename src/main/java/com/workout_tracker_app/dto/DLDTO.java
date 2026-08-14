package com.workout_tracker_app.dto;

import com.workout_tracker_app.Entity.DailyLog.Status;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DLDTO {

    private String day;
    private LocalDate date;
    private String exerciseName;
    private String muscleGroup;
    private Integer weights;
    private Integer sets;
    private Integer reps;
    private Integer duration;
    private Integer comment;
    private Status status;

    // For Queries with only day, date, exerciseName, and status
    public DLDTO(String day, LocalDate date, String exerciseName, Status status) {
        this.day = day;
        this.date = date;
        this.exerciseName = exerciseName;
        this.status = status;
    }

    // For Muscle Group Queries
    public DLDTO(String day, LocalDate date, String exerciseName, String muscleGroup, Status status) {
        this.day = day;
        this.date = date;
        this.exerciseName = exerciseName;
        this.muscleGroup = muscleGroup;
        this.status = status;
    }

    // For Queries with all details excluding muscleGroup
    public DLDTO(String day, LocalDate date, String exerciseName, Integer weights, Integer sets,
            Integer reps, Integer duration, Integer comment, Status status) {
        this.day = day;
        this.date = date;
        this.exerciseName = exerciseName;
        this.weights = weights;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.comment = comment;
        this.status = status;
    }
}