package com.workout_tracker_app.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.workout_tracker_app.Entity.ExerciseList;
import com.workout_tracker_app.Entity.ExerciseList.exercise_category;
import com.workout_tracker_app.Entity.ExerciseList.exercise_type;

@Repository
public interface Exerciserepo extends JpaRepository<ExerciseList, Long> {

        // Category section
        @Query("SELECT e FROM ExerciseList e WHERE e.category = :category")
        List<ExerciseList> findByCategory(@Param("category") String category);

        @Query("SELECT e FROM ExerciseList e WHERE e.category IS NOT NULL")
        List<ExerciseList> findByAllCategory();

        // Name section
        @Query(value = "SELECT * FROM workoutapp.exercise_list WHERE exercise_name = :exerciseName ORDER BY exercise_id LIMIT 1", nativeQuery = true)
        ExerciseList findByName(@Param("exerciseName") String exerciseName);

        // Type section

        @Query("SELECT e FROM ExerciseList e WHERE e.exercise_type = :exerciseType")
        List<ExerciseList> findByType(@Param("exerciseType") String exerciseType);

        @Query("SELECT e FROM ExerciseList e WHERE e.exercise_type IS NOT NULL")
        List<ExerciseList> findByAllType();

        // Muscle-group section

        @Query("SELECT e FROM ExerciseList e WHERE e.muscle_group = :muscleGroup")
        List<ExerciseList> findByMuscleGroup(@Param("muscleGroup") String muscleGroup);

        @Query("SELECT e FROM ExerciseList e WHERE e.muscle_group IS NOT NULL")
        List<ExerciseList> findByAllMuscleGroup();

        // Id section
        @Query("SELECT e FROM ExerciseList e WHERE e.exercise_id = :exerciseId")
        Optional<ExerciseList> findByExerciseId(@Param("exerciseId") Long exerciseId);

        @Query("SELECT e FROM ExerciseList e WHERE e.exercise_name IS NOT NULL")
        List<ExerciseList> findAll();

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO workoutapp.exercise_list(exercise_name, etype, muscle_group, category)
                        VALUES (:exercise_name, :etype, :muscle_group, :category)""", nativeQuery = true)
        void insertIntoExerciseList(@Param("exercise_name") String exercise_name,
                        @Param("etype") String etype,
                        @Param("muscle_group") String muscle_group, @Param("category") String category);

}
