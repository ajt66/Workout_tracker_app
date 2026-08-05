package com.workout_tracker_app.Entity;

import java.util.List;

import com.workout_tracker_app.Entity.ExerciseList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "programs")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Program {

    public enum Tag {
        PUSH, PULL, LEGS, FULL_BODY, UPPER, LOWER, BRO_SPLIT, REST, CHEST, ARMS, BACK, SHOULDERS,
        CARDIO, CORE, STRETCHING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programId;

    @OneToMany(mappedBy = "programs", fetch = FetchType.LAZY, orphanRemoval = false)
    private List<ExerciseList> exerciseId;

    @Column(name = "day", nullable = true)
    private String day;

    @Column(name = "sets", nullable = true)
    private Integer sets;

    @Column(name = "reps", nullable = true)
    private Integer reps;

    @Column(name = "duration", nullable = true)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = true)
    private Tag tag;

}
