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
    @Query(value = "SELECT e FROM exercise_list e WHERE e.category = :category")
    List<ExerciseList> findByCategoryWithdetails(Category category);

    @Query(value = "SELECT e.exercise_name FROM exercise_list e WHERE e.category = :category")
    List<ExerciseList> findByCategory(Category category);

    @Query(value = "SELECT e.exercise_name, e.category FROM exercise_list e WHERE e.category IS NOT NULL")
    List<ExerciseList> findByAllCategory();

    // Name section
    @Query(value = "SELECT e FROM exercise_list e WHERE e.exercise_name = :exercise_name")
    List<ExerciseList> findByNameWithDetails(String exercise_name);

    @Query(value = "SELECT e.exercise_name FROM exercise_list e WHERE e.exercise_name = :exercise_name")
    List<ExerciseList> findByName(String exercise_name);

    // Type section
    @Query(value = "SELECT e FROM exercise_list e WHERE e.exercise_type = :exercise_type")
    List<ExerciseList> findByTypeWithDetails(Type exercise_type);

    @Query(value = "SELECT e.exercise_name FROM exercise_list e WHERE e.exercise_type = :exercise_type")
    List<ExerciseList> findByType(Type exercise_type);

    @Query(value = "SELECT e.exercise_name, e.exercise_type FROM exercise_list e WHERE e.exercise_type IS NOT NULL")
    List<ExerciseList> findByAllType();

    // Muscle-group section
    @Query(value = "SELECT e FROM exercise_list e WHERE e.muscle_group = :muscle_group")
    List<ExerciseList> findByMuscleGroupWithDetails(String muscle_group);

    @Query(value = "SELECT e.exercise_name FROM exercise_list e WHERE e.muscle_group = :muscle_group")
    List<ExerciseList> findByMuscleGroup(String muscle_group);

    @Query(value = "SELECT e.exercise_name, e.muscle_group FROM exercise_list e WHERE e.muscle_group IS NOT NULL")
    List<ExerciseList> findByAllMuscleGroup();

    // Id section
    @Query(value = "SELECT e FROM exercise_list e WHERE e.exercise_id = :exercise_id")
    Optional<ExerciseList> findByExerciseIdWithDetails(Long exercise_id);

    @Query(value = "SELECT e.exercise_name FROM exercise_list e WHERE e.exercise_id = :exercise_id")
    Optional<ExerciseList> findByExerciseId(Long exercise_id);
}
