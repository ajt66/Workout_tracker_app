package com.workout_tracker_app.Entity;

import jakarta.persistence.CascadeType;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program_log")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class programLog {

    public enum program_tag {
        push, pull, legs, full_body, upper, lower, bro_split, rest, chest, arms, back, shoulders,
        cardio, core, stretching, none
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plog_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", nullable = false)
    private ExerciseList exercise_id;

    @Column(name = "day", nullable = false)
    private String day;

    @Column(name = "sets", nullable = true)
    private Integer sets;

    @Column(name = "reps", nullable = true)
    private Integer reps;

    @Column(name = "duration", nullable = true)
    private String duration;

    @Column(name = "tag", nullable = false)
    private String program_tag;
}
