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
        cardio, strength, flexibility, hypertrophy, isometrics, hypertrophyorstrength
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exercise_id;

    @Column(name = "exercise_name", nullable = false)
    private String exercise_name; // Name of exercise

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = true)
    private Type exercise_type; // Type: compound or isolation

    @Column(name = "muscle_group", nullable = true)
    private String muscle_group; // Related muscle group of exercise.

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category; // cardio, strength, flexibility

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dailylog_id", referencedColumnName = "log_id", nullable = false)
    private DailyLog dailylog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programlog_id", referencedColumnName = "plog_id", nullable = false)
    private programLog program_log;

}