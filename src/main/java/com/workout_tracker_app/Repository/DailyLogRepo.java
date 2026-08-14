package com.workout_tracker_app.Repository;

import com.workout_tracker_app.Entity.DailyLog;
import com.workout_tracker_app.dto.DLDTO;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DailyLogRepo extends JpaRepository<DailyLog, Long> {

        // LogId section
        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.logId = :logId")
        List<DLDTO> findByLogId(@Param("logId") Long logId);

        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.weights, d.sets, d.reps, d.duration, d.comment, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.logId = :logId")
        List<DLDTO> findByLogIdWithDetails(@Param("logId") Long logId);

        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.logId = :logId")
        List<DLDTO> findByListOfLogId(@Param("logId") Long logId);

        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.weights, d.sets, d.reps, d.duration, d.comment, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.logId = :logId")
        List<DLDTO> findByListOfLogIdWithDetails(@Param("logId") Long logId);

        // Day section
        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.day = :day")
        List<DLDTO> findByDay(@Param("day") String day);

        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.weights, d.sets, d.reps, d.duration, d.comment, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.day = :day")
        List<DLDTO> findByDayWithDetails(@Param("day") String day);

        // Date section
        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.date = :date")
        List<DLDTO> findByDate(@Param("date") String date);

        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.weights, d.sets, d.reps, d.duration, d.comment, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.date = :date")
        List<DLDTO> findByDateWithDetails(@Param("date") String date);

        // Muscle Group section
        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, e.muscleGroup, d.weights, d.sets, d.reps, d.duration, d.comment, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE e.muscleGroup = :muscleGroup")
        List<DLDTO> findByExercisesMuscleGroupWithDetails(@Param("muscleGroup") String muscleGroup);

        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, e.muscleGroup, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE e.muscleGroup = :muscleGroup")
        List<DLDTO> findByExercisesMuscleGroup(@Param("muscleGroup") String muscleGroup);

        // Exercise Name section
        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE e.name = :name")
        List<DLDTO> findByExercisesName(@Param("name") String name);

        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.weights, d.sets, d.reps, d.duration, d.comment, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE e.name = :name")
        List<DLDTO> findByExercisesNameWithDetails(@Param("name") String name);

        // Status section
        @Query(value = "SELECT new DLDTO(d.day, d.date, e.name, d.status)"
                        + " FROM DailyLog d JOIN ExerciseList e ON d.logId = e.dailyLog.logId"
                        + " WHERE d.status = :status")
        List<DLDTO> findByStatus(@Param("status") String status);
}
