package com.workout_tracker_app.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exerciselist")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseList {
    public enum Type {
        isolation, compound
    }

    public enum Category {
        cardio, strength, flexibility, hypertrophy, isometrics
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exerciseId;

    @Column(name = "exercise_name", nullable = false)
    private String name; // Name of exercise

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = true)
    private Type type; // Type: compound or isolation

    @Column(name = "muscle_group", nullable = true)
    private String muscleGroup; // Related muscle group of exercise.

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category; // cardio, strength, flexibility

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dailylog_id", referencedColumnName = "logId", nullable = false)
    private DailyLog dailyLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programs_id", referencedColumnName = "programId", nullable = false)
    private Program programs;
}