package com.workout_tracker_app.Entity;

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

    public enum program_status {
        active, inactive
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long program_id;

    @Column(name = "program_name", nullable = false)
    private String program_name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User users;

    @OneToMany(mappedBy = "programDL", fetch = FetchType.LAZY, orphanRemoval = false)
    private List<DailyLog> daily_log;

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY, orphanRemoval = false)
    private List<programLog> program_log;

    @Column(name = "pstatus", nullable = false)
    private String program_status;

}
