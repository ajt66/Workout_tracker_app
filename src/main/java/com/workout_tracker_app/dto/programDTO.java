package com.workout_tracker_app.dto;

import com.workout_tracker_app.Entity.Program.Status;
import com.workout_tracker_app.Entity.Program.Tag;
import com.workout_tracker_app.Entity.ExerciseList.Type;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class programDTO {

    private String programName;
    private String day;
    private String exerciseName;
    private Integer sets;
    private Integer reps;
    private Type type;
    private Integer duration;
    private Tag tag;
    private Status status;

    // For Queries with only programName, day, exerciseName, and status
    public programDTO(String programName, String day, String exerciseName, Type type, Status status) {
        this.programName = programName;
        this.day = day;
        this.exerciseName = exerciseName;
        this.type = type;
        this.status = status;
    }
}
