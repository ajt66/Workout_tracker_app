package com.workout_tracker_app.dto;

import com.workout_tracker_app.Entity.DailyLog.log_status;
import com.workout_tracker_app.Entity.programLog.program_tag;

import java.time.LocalDate;

import org.springframework.data.annotation.PersistenceCreator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DLDTO {

    private Long log_id;
    private String day;
    private LocalDate logging_date;
    private String exercise_name;
    private Integer weights;
    private Integer sets;
    private Integer reps;
    private String duration;
    private String comment;
    private String log_status;
    private String program_tag;

    public DLDTO() {
    }

    public DLDTO(Long log_id, LocalDate logging_date, String day, String exercise_name,
            Integer weights, Integer sets, Integer reps, String duration, String comment, String log_status) {
        this.log_id = log_id;
        this.logging_date = logging_date;
        this.day = day;
        this.exercise_name = exercise_name;
        this.weights = weights;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.comment = comment;
        this.log_status = log_status;
    }

    @PersistenceCreator
    public DLDTO(Long log_id, String day, LocalDate logging_date, String exercise_name,
            Integer weights, Integer sets,
            Integer reps, String duration, String comment, String log_status, String program_tag) {
        this.log_id = log_id;
        this.day = day;
        this.logging_date = logging_date;
        this.exercise_name = exercise_name;
        this.weights = weights;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.comment = comment;
        this.log_status = log_status;
        this.program_tag = program_tag;
    }

    public DLDTO(String day, LocalDate logging_date, String exercise_name,
            Integer weights, Integer sets,
            Integer reps, String duration, String comment, String log_status) {
        this.day = day;
        this.logging_date = logging_date;
        this.exercise_name = exercise_name;
        this.weights = weights;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.comment = comment;
        this.log_status = log_status;
    }

    public DLDTO(Long log_id, String day, LocalDate logging_date, String exercise_name, String log_status) {
        this.log_id = log_id;
        this.day = day;
        this.logging_date = logging_date;
        this.exercise_name = exercise_name;
        this.log_status = log_status;
    }

    public DLDTO(String day, LocalDate logging_date, String exercise_name, String log_status) {
        this.day = day;
        this.logging_date = logging_date;
        this.exercise_name = exercise_name;
        this.log_status = log_status;
    }

}