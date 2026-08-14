package com.workout_tracker_app.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.ExerciseList.Category;
import com.workout_tracker_app.Entity.ExerciseList.Type;

@Repository
public interface Exerciserepo extends JpaRepository<ExerciseList, Long> {

    // Category section
    @Query(value = "SELECT e FROM ExerciseList e WHERE e.category = :category")
    List<ExerciseList> findByCategoryWithdetails(Category category);

    @Query(value = "SELECT e.name FROM ExerciseList e WHERE e.category = :category")
    List<ExerciseList> findByCategory(Category category);

    @Query(value = "SELECT e.name, e.category FROM ExerciseList e WHERE e.category IS NOT NULL")
    List<ExerciseList> findByAllCategory();

    // Name section
    @Query(value = "SELECT e FROM ExerciseList e WHERE e.name = :name")
    List<ExerciseList> findByNameWithDetails(String name);

    @Query(value = "SELECT e.name FROM ExerciseList e WHERE e.name = :name")
    List<ExerciseList> findByName(String name);

    // Type section
    @Query(value = "SELECT e FROM ExerciseList e WHERE e.type = :type")
    List<ExerciseList> findByTypeWithDetails(Type type);

    @Query(value = "SELECT e.name FROM ExerciseList e WHERE e.type = :type")
    List<ExerciseList> findByType(Type type);

    @Query(value = "SELECT e.name, e.type FROM ExerciseList e WHERE e.type IS NOT NULL")
    List<ExerciseList> findByAllType();

    // Muscle-group section
    @Query(value = "SELECT e FROM ExerciseList e WHERE e.muscleGroup = :muscleGroup")
    List<ExerciseList> findByMuscleGroupWithDetails(String muscleGroup);

    @Query(value = "SELECT e.name FROM ExerciseList e WHERE e.muscleGroup = :muscleGroup")
    List<ExerciseList> findByMuscleGroup(String muscleGroup);

    @Query(value = "SELECT e.name, e.muscle_group FROM ExerciseList e WHERE e.muscleGroup IS NOT NULL")
    List<ExerciseList> findByAllMuscleGroup();

    // Id section
    @Query(value = "SELECT e FROM ExerciseList e WHERE e.exerciseId = :exerciseId")
    Optional<ExerciseList> findByExerciseIdWithDetails(Long exerciseId);

    @Query(value = "SELECT e.name FROM ExerciseList e WHERE e.exerciseId = :exerciseId")
    Optional<ExerciseList> findByExerciseId(Long exerciseId);
}
