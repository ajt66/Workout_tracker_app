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
@Table(name = "exercise_list")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseList {
    public enum exercise_type {
        isolation, compound, none
    }

    public enum exercise_category {
        cardio, strength, flexibility, hypertrophy, isometrics, hypertrophyorstrength, none
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exercise_id;

    @Column(name = "exercise_name", nullable = false)
    private String exercise_name; // Name of exercise

    @Column(name = "etype", nullable = true)
    private String exercise_type; // Type: compound or isolation

    @Column(name = "muscle_group", nullable = true)
    private String muscle_group; // Related muscle group of exercise.

    @Column(name = "category", nullable = false)
    private String category; // cardio, strength, flexibility

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User users;

}