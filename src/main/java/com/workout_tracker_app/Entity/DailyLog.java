package com.workout_tracker_app.Entity;

import java.time.LocalDate;
import java.util.List;

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
@Table(name = "dailylog")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog {
    public enum Status {
        PENDING, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long log_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id", nullable = false)
    private User users;

    @Column(name = "day", nullable = false)
    private String day;

    @Column(name = "logging_date", nullable = false)
    private LocalDate logging_date;

    @OneToOne(mappedBy = "dailylog")
    private programLog pLog;

    @OneToMany(mappedBy = "dailylog", fetch = FetchType.LAZY, orphanRemoval = false)
    private List<ExerciseList> exercise_id;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "log_status", nullable = false)
    private Status log_status;
}
