package com.workout_tracker_app.Entity;

import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    public enum Tag {
        push, pull, legs, full_body, upper, lower, bro_split, rest, chest, arms, back, shoulders,
        cardio, core, stretching, none
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plog_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false)
    private Program program;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dailylog_id", referencedColumnName = "log_id")
    private DailyLog dailylog;

    @OneToMany(mappedBy = "program_log", fetch = FetchType.LAZY, orphanRemoval = false)
    private List<ExerciseList> exercise_id;

    @Column(name = "day", nullable = false)
    private String day;

    @Column(name = "sets", nullable = true)
    private Integer sets;

    @Column(name = "reps", nullable = true)
    private Integer reps;

    @Column(name = "duration", nullable = true)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = false)
    private Tag program_tag;
}
