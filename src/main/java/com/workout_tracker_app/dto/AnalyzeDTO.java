package com.workout_tracker_app.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AnalyzeDTO {
    private LocalDate dates;
    private String name;
    private Long volume;
    private Long weights;
    private Long sets;
    private Long reps;

    public AnalyzeDTO(LocalDate dates, String name, Long volume, Long weights, Long sets, Long reps) {
        this.dates = dates;
        this.name = name;
        this.volume = volume;
        this.weights = weights;
        this.sets = sets;
        this.reps = reps;
    }

    public AnalyzeDTO(LocalDate dates, Long volume, Long weights, Long sets, Long reps) {
        this.dates = dates;
        this.volume = volume;
        this.weights = weights;
        this.sets = sets;
        this.reps = reps;
    }

    public AnalyzeDTO(Long volume, Long weights, Long sets, Long reps) {
        this.volume = volume;
        this.weights = weights;
        this.sets = sets;
        this.reps = reps;
    }

    public AnalyzeDTO(Long weights, Long sets, Long reps) {
        this.weights = weights;
        this.sets = sets;
        this.reps = reps;
    }

}
