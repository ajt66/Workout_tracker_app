package com.workout_tracker_app.Entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dailylog", uniqueConstraints = { @UniqueConstraint(columnNames = { "Exerciseid" }) })
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog {
    enum Status {
        PENDING, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Logid;

    @Column(name = "Day", nullable = false)
    private String Day;

    @Column(name = "Logging_Date", nullable = false)
    private LocalDate Date;

    @OneToMany(mappedBy = "dailyLog", fetch = FetchType.LAZY, orphanRemoval = false)
    private List<ExerciseList> exerciseID;

    @Column(name = "Weights", nullable = true)
    private int weights;

    @Column(name = "Sets", nullable = true)
    private int sets;

    @Column(name = "Reps", nullable = true)
    private int reps;

    @Column(name = "Duration", nullable = true)
    private String duration;

    @Column(name = "Comment", nullable = true)
    private String Comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = true)
    private Status Status;
}
