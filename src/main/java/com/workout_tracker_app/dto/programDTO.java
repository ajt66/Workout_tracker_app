package com.workout_tracker_app.dto;

import org.springframework.data.annotation.PersistenceCreator;

import com.workout_tracker_app.Entity.ExerciseList.Type;
import com.workout_tracker_app.Entity.Program.Status;
import com.workout_tracker_app.Entity.programLog.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class programDTO {

    private Long program_id;
    private String program_name;
    private String day;
    private String exercise_name;
    private Integer sets;
    private Integer reps;
    private Type exercise_type;
    private Integer duration;
    private Tag program_tag;
    private Status program_status;

    public programDTO() {
    }

    @PersistenceCreator
    public programDTO(Long program_id, String program_name, String day, String exercise_name, Integer sets,
            Integer reps, Type exercise_type, Integer duration, Tag program_tag, Status program_status) {
        this.program_id = program_id;
        this.program_name = program_name;
        this.day = day;
        this.exercise_name = exercise_name;
        this.sets = sets;
        this.reps = reps;
        this.exercise_type = exercise_type;
        this.duration = duration;
        this.program_tag = program_tag;
        this.program_status = program_status;
    }

    // For Queries with only programName, day, exerciseName, and status
    public programDTO(Long program_id, String program_name, String day, String exercise_name, Tag program_tag) {
        this.program_name = program_name;
        this.day = day;
        this.exercise_name = exercise_name;
        this.program_tag = program_tag;
        this.program_id = program_id;
    }

    public programDTO(Long program_id) {
        this.program_id = program_id;
    }

    public programDTO(Long program_id, String program_name, String day, String exercise_name, Type exercise_type) {
        this.program_name = program_name;
        this.day = day;
        this.exercise_name = exercise_name;
        this.exercise_type = exercise_type;
    }

    public programDTO(Long program_id, String program_name, Status program_status) {
        this.program_name = program_name;
        this.program_status = program_status;
        this.program_id = program_id;
    }

    public programDTO(Long program_id, String program_name, String day, String exercise_name, Type exercise_type,
            Integer sets,
            Integer reps,
            Integer duration, Tag program_tag) {
        this.program_id = program_id;
        this.program_name = program_name;
        this.day = day;
        this.exercise_name = exercise_name;
        this.exercise_type = exercise_type;
        this.reps = reps;
        this.sets = sets;
        this.duration = duration;
        this.program_tag = program_tag;
    }

    public programDTO(Long program_id, String program_name, String day, String exercise_name, Type exercise_type,
            Integer sets,
            Integer reps,
            Integer duration) {
        this.program_name = program_name;
        this.day = day;
        this.exercise_name = exercise_name;
        this.exercise_type = exercise_type;
        this.reps = reps;
        this.sets = sets;
        this.duration = duration;
        this.program_id = program_id;
    }

}
