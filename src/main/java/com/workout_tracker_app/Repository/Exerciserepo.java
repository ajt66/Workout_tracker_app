package com.workout_tracker_app.Repository;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.ExerciseList.Category;
import com.workout_tracker_app.Entity.ExerciseList.Type;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface Exerciserepo extends JpaRepository<ExerciseList, Long> {
    List<ExerciseList> findByCategory(Category category);

    List<ExerciseList> findByName(String name);

    List<ExerciseList> findByType(Type type);

    List<ExerciseList> findByMuscleGroup(String muscleGroup);

    Optional<ExerciseList> findByExerciseId(Long exerciseId);
}
