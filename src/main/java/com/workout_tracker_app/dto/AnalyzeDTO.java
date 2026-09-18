package com.workout_tracker_app.dto;

import org.springframework.data.annotation.PersistenceCreator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyzeDTO {
    private String Dates;
    private Long Total_weight;
    private Long Total_sets;
    private Long Total_reps;
    private Long Total_volume;

    @PersistenceCreator
    public AnalyzeDTO(String Dates, Long Total_weight, Long Total_sets,
            Long Total_reps, Long Total_volume) {
        this.Dates = Dates;
        this.Total_weight = Total_weight;
        this.Total_sets = Total_sets;
        this.Total_reps = Total_reps;
        this.Total_volume = Total_volume;
    }

    public AnalyzeDTO() {
    }

    public AnalyzeDTO(String Dates, Long Total_weight, Long Total_sets, Long Total_reps) {
        this.Dates = Dates;
        this.Total_weight = Total_weight;
        this.Total_sets = Total_sets;
        this.Total_reps = Total_reps;
    }

    public AnalyzeDTO(Long Total_weight, Long Total_sets, Long Total_reps) {
        this.Total_weight = Total_weight;
        this.Total_sets = Total_sets;
        this.Total_reps = Total_reps;
    }

    public AnalyzeDTO(String Dates, Long Total_volume) {
        this.Dates = Dates;
        this.Total_volume = Total_volume;
    }

    public AnalyzeDTO(Long Total_volume) {
        this.Total_volume = Total_volume;
    }

}
