package com.workout_tracker_app.Entity;

import java.time.LocalDate;
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
@Table(name = "daily_log")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog {
    public enum log_status {
        pending, completed, cancelled
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long log_id;

    @Column(name = "day", nullable = false)
    private String day;

    @Column(name = "logging_date", nullable = false)
    private LocalDate logging_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false)
    private Program programDL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id")
    private ExerciseList exercise_id;

    @Column(name = "weights", nullable = true)
    private Integer weights;

    @Column(name = "sets", nullable = true)
    private Integer sets;

    @Column(name = "reps", nullable = true)
    private Integer reps;

    @Column(name = "duration", nullable = true)
    private String duration;

    @Column(name = "comment", nullable = true)
    private String comment;

    @Column(name = "dstatus", nullable = false)
    private String log_status;
}
