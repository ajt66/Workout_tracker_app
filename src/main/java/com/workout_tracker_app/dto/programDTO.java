package com.workout_tracker_app.dto;

import org.springframework.data.annotation.PersistenceCreator;

import com.workout_tracker_app.Entity.ExerciseList.exercise_type;
import com.workout_tracker_app.Entity.Program.program_status;
import com.workout_tracker_app.Entity.programLog.program_tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class programDTO {

    private Long program_id;
    private String program_name;
    private Long plog_id;
    private String day;
    private String exercise_name;
    private Integer sets;
    private Integer reps;
    private String exercise_type;
    private String duration;
    private String program_tag;
    private String program_status;

    public programDTO() {
    }

    @PersistenceCreator
    public programDTO(Long program_id, String program_name, Long plog_id, String day, String exercise_name,
            String exercise_type, Integer sets,
            Integer reps, String duration, String program_tag, String program_status) {
        this.program_id = program_id;
        this.program_name = program_name;
        this.plog_id = plog_id;
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
    public programDTO(Long program_id, String program_name, String day, String exercise_name, String program_tag,
            String exercise_type) {
        this.program_name = program_name;
        this.day = day;
        this.exercise_name = exercise_name;
        this.program_tag = program_tag;
        this.program_id = program_id;
        this.exercise_type = exercise_type;
    }

    public programDTO(Long program_id) {
        this.program_id = program_id;
    }

    public programDTO(Long program_id, String program_name, String program_status) {
        this.program_name = program_name;
        this.program_status = program_status;
        this.program_id = program_id;
    }

    public programDTO(Long program_id, String program_name, String day, String exercise_name, String exercise_type,
            Integer sets,
            Integer reps,
            String duration, String program_tag, String program_status) {
        this.program_id = program_id;
        this.program_name = program_name;
        this.day = day;
        this.exercise_name = exercise_name;
        this.exercise_type = exercise_type;
        this.reps = reps;
        this.sets = sets;
        this.duration = duration;
        this.program_tag = program_tag;
        this.program_status = program_status;
    }

    public programDTO(Long program_id, String program_name, String day, String exercise_name, String exercise_type,
            Integer sets,
            Integer reps,
            String duration) {
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
