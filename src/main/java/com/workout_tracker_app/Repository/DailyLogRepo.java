package com.workout_tracker_app.Repository;

import com.workout_tracker_app.Entity.DailyLog;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DailyLogRepo extends JpaRepository<DailyLog, Long> {

    // LogId section
    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE d.logId = :logId", nativeQuery = true)
    Optional<Map<String, Object>> findByLogId(@Param("logId") Long logId);

    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.weights, d.sets, d.reps, d.duration, d.comment, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE d.logId = :logId", nativeQuery = true)
    List<Map<String, Object>> findByLogIdWithDetails(@Param("logId") Long logId);

    // Day section
    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE d.day = :day", nativeQuery = true)
    List<Map<String, Object>> findByDay(@Param("day") String day);

    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.weights, d.sets, d.reps, d.duration, d.comment, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE d.day = :day", nativeQuery = true)
    List<Map<String, Object>> findByDayWithDetails(@Param("day") String day);

    // Date section
    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE d.date = :date", nativeQuery = true)
    List<Map<String, Object>> findByDate(@Param("date") String date);

    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.weights, d.sets, d.reps, d.duration, d.comment, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE d.date = :date", nativeQuery = true)
    List<Map<String, Object>> findByDateWithDetails(@Param("date") String date);

    // Muscle Group section
    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.weights, d.sets, d.reps, d.duration, d.comment, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE e.muscleGroup = :muscleGroup", nativeQuery = true)
    List<Map<String, Object>> findByExercisesMuscleGroupWithDetails(@Param("muscleGroup") String muscleGroup);

    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE e.muscleGroup = :muscleGroup", nativeQuery = true)
    List<Map<String, Object>> findByExercisesMuscleGroup(@Param("muscleGroup") String muscleGroup);

    // Exercise Name section
    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE e.name = :name", nativeQuery = true)
    List<Map<String, Object>> findByExercisesName(@Param("name") String name);

    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.weights, d.sets, d.reps, d.duration, d.comment, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE e.name = :name", nativeQuery = true)
    List<Map<String, Object>> findByExercisesNameWithDetails(@Param("name") String name);

    // Status section
    @Query(value = "SELECT d.date, e.name, e.muscleGroup, d.status"
            + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
            + " WHERE d.status = :status", nativeQuery = true)
    List<Map<String, Object>> findByStatus(@Param("status") String status);
}
