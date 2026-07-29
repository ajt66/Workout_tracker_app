package com.workout_tracker_app.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Name", nullable = false)
    private String Name; // Name of exercise

    @Column(name = "Type", nullable = false)
    private String Type; // Type: compound or isolation

    @Column(name = "Muscle_Group", nullable = false)
    private String Muscle_Group; // Related muscle group of exercise

    @Column(name = "Category", nullable = false)
    private String Category; // Push, Pull or Legs

}