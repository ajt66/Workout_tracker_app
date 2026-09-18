package com.workout_tracker_app.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.ExerciseList.Category;
import com.workout_tracker_app.Entity.ExerciseList.Type;

@Repository
public interface Exerciserepo extends JpaRepository<ExerciseList, Long> {

    // Category section
    @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.category = :category AND u.id = :user_id")
    List<ExerciseList> findByCategoryWithUser(@Param("category") Category category, @Param("user_id") Long user_id);

    @Query("SELECT e FROM ExerciseList e WHERE e.category = :category")
    List<ExerciseList> findByCategory(@Param("category") Category category);

    @Query("SELECT e FROM ExerciseList e WHERE e.category IS NOT NULL")
    List<ExerciseList> findByAllCategory();

    // Name section
    @Query("SELECT e FROM ExerciseList e WHERE e.exercise_name = :exerciseName")
    List<ExerciseList> findByName(@Param("exerciseName") String exerciseName);

    @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.exercise_name = :exerciseName AND u.id = :user_id")
    List<ExerciseList> findByNameWithUser(@Param("exerciseName") String exerciseName, @Param("user_id") Long user_id);

    // Type section
    @Query("SELECT e FROM ExerciseList e WHERE e.exercise_type = :exerciseType")
    List<ExerciseList> findByType(@Param("exerciseType") Type exerciseType);

    @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.exercise_type = :exerciseType AND u.id = :user_id")
    List<ExerciseList> findByTypeWithUser(@Param("exerciseType") Type exerciseType, @Param("user_id") Long user_id);

    @Query("SELECT e FROM ExerciseList e WHERE e.exercise_type IS NOT NULL")
    List<ExerciseList> findByAllType();

    // Muscle-group section

    @Query("SELECT e FROM ExerciseList e WHERE e.muscle_group = :muscleGroup")
    List<ExerciseList> findByMuscleGroup(@Param("muscleGroup") String muscleGroup);

    @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.muscle_group = :muscleGroup AND u.id = :user_id")
    List<ExerciseList> findByMuscleGroupWithUser(@Param("muscleGroup") String muscleGroup,
            @Param("user_id") Long user_id);

    @Query("SELECT e FROM ExerciseList e WHERE e.muscle_group IS NOT NULL")
    List<ExerciseList> findByAllMuscleGroup();

    // Id section
    @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.exercise_id = :exerciseId AND u.id = :user_id")
    Optional<ExerciseList> findByExerciseId(@Param("exerciseId") Long exerciseId, @Param("user_id") Long user_id);
}
