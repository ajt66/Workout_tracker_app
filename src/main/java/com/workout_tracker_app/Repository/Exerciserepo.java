package com.workout_tracker_app.Repository;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface Exerciserepo extends JpaRepository<ExerciseList, Long> {
    Optional<ExerciseList> findByExercisename(String exercisename);
}
