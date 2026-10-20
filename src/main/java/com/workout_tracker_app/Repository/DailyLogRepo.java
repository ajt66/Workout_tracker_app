package com.workout_tracker_app.Repository;

import com.workout_tracker_app.Entity.DailyLog;
import com.workout_tracker_app.dto.DLDTO;
import com.workout_tracker_app.dto.AnalyzeDTO;
import com.workout_tracker_app.Entity.DailyLog.log_status;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DailyLogRepo extends JpaRepository<DailyLog, Long> {

        public static final String TABLESELECT = "FROM DailyLog d JOIN d.exercise_id e"
                        + " JOIN d.programDL p" + " JOIN p.users u";

        // LogId section
        @Query(value = "SELECT new com.workout_tracker_app.dto.DLDTO(d.log_id, d.day, d.logging_date, e.exercise_name, d.log_status)"
                        + TABLESELECT
                        + " WHERE d.log_id = :log_id AND u.id = :user_id")
        List<DLDTO> findByLogId(@Param("log_id") Long log_id, @Param("user_id") Long user_id);

        @Query(value = "SELECT new com.workout_tracker_app.dto.DLDTO(d.day, d.logging_date, e.exercise_name, d.weights, d.sets, d.reps, d.duration, d.comment, d.log_status)"
                        + TABLESELECT
                        + " WHERE d.log_id = :log_id AND u.id = :user_id")
        List<DLDTO> findByLogIdWithDetails(@Param("log_id") Long log_id, @Param("user_id") Long user_id);

        // Status section
        @Query(value = "SELECT new com.workout_tracker_app.dto.DLDTO(d.day, d.logging_date, e.exercise_name, d.log_status)"
                        + TABLESELECT
                        + " WHERE d.log_status = :log_status AND u.id = :user_id")
        List<DLDTO> findByStatus(@Param("log_status") String log_status, @Param("user_id") Long user_id);

        // Daily program log maintenance
        // --------------------------------------------------------------------------------------------------------------------------------------------------
        @Modifying
        @Transactional
        @Query(value = """
                        INSERT INTO workoutapp.daily_log (program_id, day, logging_date, exercise_id, sets, reps, duration, dstatus)
                        SELECT pl.program_id, pl.day, CURRENT_DATE, pl.exercise_id, pl.sets, pl.reps, pl.duration, 'pending'
                        FROM workoutapp.programs p
                        JOIN workoutapp.program_log pl ON pl.program_id = p.program_id
                        WHERE p.user_id = :userId AND p.pstatus = 'active' AND pl.day = :day
                        """, nativeQuery = true)
        int copyProgramDataForToday(@Param("userId") Long userId, @Param("day") String day);

        @Modifying
        @Transactional
        @Query(value = """
                        UPDATE workoutapp.daily_log
                        SET dstatus = 'cancelled'
                        WHERE dstatus = 'pending' AND logging_date < CURRENT_DATE
                        AND program_id IN (
                                SELECT program_id FROM workoutapp.programs
                                WHERE user_id = :userId AND pstatus = 'active'
                        )
                        """, nativeQuery = true)
        int changeStatus(@Param("userId") Long userId);

        @Query(value = """
                        SELECT COLESCE(SUM(logging_date), 0) FROM workoutapp.daily_log
                        WHERE logging_date = :date AND program_id IN (
                                SELECT program_id FROM workoutapp.programs
                                WHERE user_id = :userId AND pstatus = 'active'
                        )
                        """, nativeQuery = true)
        Long checkLogsForDate(@Param("userId") Long userId, @Param("date") LocalDate date);

        @Modifying
        @Transactional
        @Query(value = """
                        INSERT INTO workoutapp.daily_log (program_id, day, logging_date, exercise_id, sets, reps, duration, dstatus)
                        SELECT pl.program_id, :day, :date, pl.exercise_id, pl.sets, pl.reps, pl.duration, 'cancelled'
                        FROM workoutapp.programs p INNER JOIN workoutapp.program_log pl ON pl.program_id = p.program_id
                        WHERE p.user_id = :userId AND p.pstatus = 'active' AND pl.day = :day
                        """, nativeQuery = true)
        void insertEmptyLogs(@Param("userId") Long userId, @Param("date") LocalDate date, @Param("day") String day);

        // Native Query
        // --------------------------------------------------------------------------------------------------------------------------------------------------------
        @Query(value = "SELECT d.log_id, d.logging_date, d.day, e.exercise_name, d.weights, d.sets,"
                        + " d.reps, d.duration, d.comment, d.dstatus"
                        + " FROM workoutapp.daily_log d JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_id"
                        + " JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id"
                        + " WHERE EXTRACT(MONTH FROM d.logging_date) = :month AND p.program_id = :program_id AND u.id = :user_id AND e.exercise_name IS NOT NULL", nativeQuery = true)
        List<DLDTO> findCurrentProgramByMonth(@Param("month") int month, @Param("user_id") Long user_id,
                        @Param("program_id") Long program_id);

        @Query(value = """
                        SELECT d.log_id, d.logging_date, d.day, e.exercise_name, d.weights, d.sets,
                        d.reps, d.duration, d.comment, d.dstatus
                        FROM workoutapp.daily_log d JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_id
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE u.id = :user_id AND p.program_id = :program_id AND e.exercise_name IS NOT NULL AND d.logging_date = CURRENT_DATE""", nativeQuery = true)
        List<DLDTO> findByCurrentDate(@Param("user_id") Long user_id, @Param("program_id") Long program_id);

        @Query(value = """
                        SELECT d.log_id, d.logging_date, d.day, e.exercise_name, d.weights, d.sets,
                        d.reps, d.duration, d.comment, d.dstatus
                        FROM workoutapp.daily_log d JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_id
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE u.id = :user_id AND p.program_id = :program_id AND e.exercise_name IS NOT NULL AND d.logging_date = :date""", nativeQuery = true)
        List<DLDTO> findByDate(@Param("user_id") Long user_id, @Param("date") LocalDate date,
                        @Param("program_id") Long program_id);

        // --------------------------------------------------------------------------------------------------------------------------------------------------
        // Updating repository
        @Modifying
        @Query(value = """
                        UPDATE workoutapp.daily_log d
                        SET exercise_id = :exercise_id, day = :day, weights=:weights, sets = :sets,
                        reps = :reps, duration = :duration, comment = :comment, dstatus = :dstatus
                        FROM workoutapp.programs p INNER JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE p.program_id = :program_id AND u.id = :user_id AND d.log_id = :log_id AND d.logging_date = :date""", nativeQuery = true)
        int updateDailyLog(@Param("user_id") Long user_id, @Param("program_id") Long program_id,
                        @Param("log_id") Long log_id,
                        @Param("exercise_id") Long exercise_id, @Param("day") String day,
                        @Param("weights") Integer weights,
                        @Param("sets") Integer sets, @Param("reps") Integer reps, @Param("duration") String duration,
                        @Param("comment") String comment, @Param("dstatus") String dstatus,
                        @Param("date") LocalDate date);

        // Inserting into repository
        @Modifying
        @Query(value = """
                        INSERT INTO workoutapp.daily_log (program_id, day, logging_date, exercise_id, weights, sets, reps, duration, comment, dstatus)
                        SELECT p.program_id, :day, :logging_date, :exercise_id, :weights, :sets, :reps, :duration, :comment, :dstatus
                        FROM workoutapp.programs p INNER JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE p.program_id = :program_id AND u.id = :user_id AND d.logging_date = :date""", nativeQuery = true)
        void insertDailyLog(@Param("user_id") Long user_id, @Param("program_id") Long program_id,
                        @Param("exercise_id") Long exercise_id, @Param("day") String day,
                        @Param("weights") Integer weights,
                        @Param("sets") Integer sets, @Param("reps") Integer reps, @Param("duration") String duration,
                        @Param("comment") String comment, @Param("dstatus") String dstatus,
                        @Param("date") LocalDate date);

        // --------------------------------------------------------------------------------------------------------------------------------------------------
        // For Analysis page
        @Query(value = """
                        SELECT COUNT(d.log_id) AS total_exercise FROM workoutapp.daily_log d
                        JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_id
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE d.dstatus = :dstatus AND u.id = :user_id AND e.exercise_name <> 'Rest And Recovery'""", nativeQuery = true)
        Long sumByExerciseAndStatus(@Param("user_id") Long user_id,
                        @Param("dstatus") String dstatus);

        @Query(value = """
                        SELECT COUNT(DISTINCT d.logging_date) AS total_days
                        FROM workoutapp.daily_log d JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_id
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE d.dstatus = :dstatus AND u.id = :user_id AND e.exercise_name <> 'Rest And Recovery'""", nativeQuery = true)
        Long countByStatus(@Param("user_id") Long user_id,
                        @Param("dstatus") String dstatus);

        @Query(value = """
                        SELECT COALESCE(SUM(d.weights * d.sets * d.reps),0) AS total_volume FROM workoutapp.daily_log d
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE d.dstatus = 'completed' AND u.id = :user_id AND d.weights IS NOT NULL AND d.sets IS NOT NULL AND d.reps IS NOT NULL;""", nativeQuery = true)
        Long sumByVolume(@Param("user_id") Long user_id);

        @Query(value = """
                        SELECT COALESCE(SUM(d.weights), 0) AS Total_Weights, COALESCE(SUM(d.sets), 0) AS Total_Sets, COALESCE(SUM(d.reps), 0) AS Total_Reps FROM workoutapp.daily_log d
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE d.dstatus = 'completed' AND u.id = :user_id AND d.weights IS NOT NULL AND d.sets IS NOT NULL AND d.reps IS NOT NULL""", nativeQuery = true)
        AnalyzeDTO TotalWSR(@Param("user_id") Long user_id);

        // For workout page
        @Query(value = """
                        SELECT TO_CHAR(d.logging_date, 'DD Mon YYYY') AS Dates, COALESCE(SUM(d.weights),0) AS Total_weight, COALESCE(SUM(d.sets),0) AS Total_sets,
                        COALESCE(SUM(d.reps),0) AS Total_reps, COALESCE(SUM(d.weights * d.sets * d.reps),0) AS Total_volume
                        FROM workoutapp.daily_log d
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE EXTRACT(MONTH FROM d.logging_date) = :month AND u.id = :user_id
                        AND d.dstatus = 'completed' GROUP BY d.logging_date""", nativeQuery = true)
        List<AnalyzeDTO> WorkoutsTabPlainQuery(@Param("month") int month,
                        @Param("user_id") Long user_id);

        @Query(value = """
                        SELECT TO_CHAR(d.logging_date, 'DD Mon YYYY') AS Dates, COALESCE(SUM(d.weights),0) AS Total_weight, COALESCE(SUM(d.sets),0) AS Total_sets,
                        COALESCE(SUM(d.reps),0) AS Total_reps, COALESCE(SUM(d.weights * d.sets * d.reps),0) AS Total_volume
                        FROM workoutapp.daily_log d JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_ida
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE EXTRACT(MONTH FROM d.logging_date) = :month AND e.category = :category AND u.id = :user_id AND d.dstatus = 'completed'
                        AND d.weights IS NOT NULL AND d.sets IS NOT NULL AND d.reps IS NOT NULL GROUP BY d.logging_date""", nativeQuery = true)
        List<AnalyzeDTO> WorkoutsTabCategoryQuery(@Param("month") int month, @Param("category") String category,
                        @Param("user_id") Long user_id);

        @Query(value = """
                        SELECT TO_CHAR(d.logging_date, 'DD Mon YYYY') AS Dates, COALESCE(SUM(d.weights),0) AS Total_weight, COALESCE(SUM(d.sets),0) AS Total_sets,
                        COALESCE(SUM(d.reps),0) AS Total_reps, COALESCE(SUM(d.weights * d.sets * d.reps),0) AS Total_volume
                        FROM workoutapp.daily_log d JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_id
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE EXTRACT(MONTH FROM d.logging_date) = :month AND e.muscle_group = :muscle_group AND u.id = :user_id AND d.dstatus = 'completed'
                        AND d.weights IS NOT NULL AND d.sets IS NOT NULL AND d.reps IS NOT NULL GROUP BY d.logging_date""", nativeQuery = true)
        List<AnalyzeDTO> WorkoutsTabMuscleGroupQuery(@Param("month") int month,
                        @Param("muscle_group") String muscle_group,
                        @Param("user_id") Long user_id);

        // For breakdown page
        @Query(value = """
                        SELECT e.muscle_group AS Exercise, COALESCE(SUM(d.weights),0) AS Total_weight, COALESCE(SUM(d.sets),0) AS Total_sets,
                        COALESCE(SUM(d.reps),0) AS Total_reps, COALESCE(SUM(d.weights * d.sets * d.reps),0) AS Total_volume
                        FROM workoutapp.daily_log d JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_id
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE EXTRACT(MONTH FROM d.logging_date) = :month AND u.id = :user_id AND d.dstatus = 'completed' AND e.muscle_group IS NOT NULL
                        AND d.weights IS NOT NULL AND d.sets IS NOT NULL AND d.reps IS NOT NULL GROUP BY e.muscle_group""", nativeQuery = true)
        List<Object[]> BreakdownTabQuery(@Param("month") int month,
                        @Param("user_id") Long user_id);

        // For exercise page
        @Query(value = """
                        SELECT DISTINCT TO_CHAR(d.logging_date, 'DD Mon YYYY') AS Dates FROM workoutapp.daily_log d
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE EXTRACT(MONTH FROM d.logging_date) = :month AND u.id = :user_id AND d.dstatus = 'completed'""", nativeQuery = true)
        List<AnalyzeDTO> ExerciseTabDates(@Param("month") int month,
                        @Param("user_id") Long user_id);

        @Query(value = """
                        SELECT TO_CHAR(d.logging_date, 'DD Mon YYYY') AS Dates, e.exercise_name AS Exercise, COALESCE(SUM(d.weights),0) AS Total_weight, COALESCE(SUM(d.sets),0) AS Total_sets,
                        COALESCE(SUM(d.reps),0) AS Total_reps, COALESCE(SUM(d.weights * d.sets * d.reps),0) AS Total_volume
                        FROM workoutapp.daily_log d JOIN workoutapp.exercise_list e ON d.exercise_id = e.exercise_id
                        JOIN workoutapp.programs p ON d.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE EXTRACT(MONTH FROM d.logging_date) = :month AND u.id = :user_id AND d.dstatus = 'completed' AND e.exercise_name = :exercise_name
                        AND d.weights IS NOT NULL AND d.sets IS NOT NULL AND d.reps IS NOT NULL GROUP BY d.logging_date, e.exercise_name""", nativeQuery = true)
        List<Object[]> ExerciseTabQuery(@Param("month") int month, @Param("exercise_name") String exercise_name,
                        @Param("user_id") Long user_id);
}
