package com.workout_tracker_app.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.ExerciseList.exercise_category;
import com.workout_tracker_app.Entity.ExerciseList.exercise_type;

@Repository
public interface Exerciserepo extends JpaRepository<ExerciseList, Long> {

        // Category section
        @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.category = :category AND u.id = :user_id")
        List<ExerciseList> findByCategory(@Param("category") String category,
                        @Param("user_id") Long user_id);

        @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.category IS NOT NULL AND u.id = :user_id")
        List<ExerciseList> findByAllCategory(@Param("user_id") Long user_id);

        // Name section
        @Query("SELECT e FROM ExerciseList e WHERE e.exercise_name = :exerciseName")
        List<ExerciseList> findByName(@Param("exerciseName") String exerciseName);

        @Query(value = "SELECT e FROM ExerciseList e JOIN e.users u WHERE e.exercise_name = :exerciseName AND u.id = :user_id")
        ExerciseList findByNameWithUser(@Param("exerciseName") String exerciseName, @Param("user_id") Long user_id);

        // Type section

        @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.exercise_type = :exerciseType AND u.id = :user_id")
        List<ExerciseList> findByType(@Param("exerciseType") String exerciseType,
                        @Param("user_id") Long user_id);

        @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.exercise_type IS NOT NULL AND u.id = :user_id")
        List<ExerciseList> findByAllType(@Param("user_id") Long user_id);

        // Muscle-group section

        @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.muscle_group = :muscleGroup AND u.id = :user_id")
        List<ExerciseList> findByMuscleGroup(@Param("muscleGroup") String muscleGroup,
                        @Param("user_id") Long user_id);

        @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.muscle_group IS NOT NULL AND u.id = :user_id")
        List<ExerciseList> findByAllMuscleGroup(@Param("user_id") Long user_id);

        // Id section
        @Query("SELECT e FROM ExerciseList e JOIN e.users u WHERE e.exercise_id = :exerciseId AND u.id = :user_id")
        Optional<ExerciseList> findByExerciseId(@Param("exerciseId") Long exerciseId, @Param("user_id") Long user_id);

        @Modifying
        @Query(value = """
                        INSERT INTO workoutapp.exercise_list(user_id, exercise_name, etype, muscle_group, category)
                        VALUES (:user_id, :exercise_name, :etype, :muscle_group, :category)""", nativeQuery = true)
        void insertIntoExerciseList(@Param("user_id") Long user_id, @Param("exercise_name") String exercise_name,
                        @Param("etype") String etype,
                        @Param("muscle_group") String muscle_group, @Param("category") String category);

}
