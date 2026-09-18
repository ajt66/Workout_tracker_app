package com.workout_tracker_app.Entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    public UserDetails convertToUserDetails() {
        return org.springframework.security.core.userdetails.User.builder()
                .username(this.username)
                .password(this.password)
                .authorities(this.role)
                .build();
    }

    @OneToMany(mappedBy = "users", fetch = FetchType.LAZY, orphanRemoval = false)
    private List<ExerciseList> exercise_id;

    @OneToMany(mappedBy = "users", fetch = FetchType.LAZY, orphanRemoval = false)
    private List<Program> program_id;

}
