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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exerciselist", uniqueConstraints = { @UniqueConstraint(columnNames = { "Exerciseid" }) })
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseList {
    enum Type {
        ISOLATION, COMPOUND
    }

    enum Category {
        CARDIO, STRENGTH, FLEXIBILTY, HYPERTROPHY, ISOMETRICS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Exerciseid;

    @Column(name = "Name", nullable = false)
    private String Name; // Name of exercise

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = true)
    private Type type; // Type: compound or isolation

    @Column(name = "Muscle-Group", nullable = true)
    private String Muscle_Group; // Related muscle group of exercise.

    @Enumerated(EnumType.STRING)
    @Column(name = "Category", nullable = false)
    private Category Category; // cardio, strength, flexibility

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dailyLog.id", nullable = false)
    private DailyLog dailyLog;
}