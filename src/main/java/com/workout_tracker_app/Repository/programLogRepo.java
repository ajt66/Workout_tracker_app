package com.workout_tracker_app.Repository;

import java.util.List;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workout_tracker_app.Entity.programLog;
import com.workout_tracker_app.dto.programDTO;

public interface programLogRepo extends JpaRepository<programLog, Long> {

        public static final String TABLESELECT = " FROM programLog plog INNER JOIN plog.exercise_id e"
                        + " INNER JOIN plog.program p" + " INNER JOIN p.users u";
        public static final String ORDER_BY = " ORDER BY CASE plog.day"
                        + " WHEN 'monday' THEN 1"
                        + " WHEN 'tuesday' THEN 2"
                        + " WHEN 'wednesday' THEN 3"
                        + " WHEN 'thursday' THEN 4"
                        + " WHEN 'friday' THEN 5"
                        + " WHEN 'saturday' THEN 6"
                        + " WHEN 'sunday' THEN 7"
                        + " END";
        /*
         * public static final String ALLPROGRAMARG =
         * "p.program_id, p.program_name, plog.day, e.exercise_name, e.exercise_type, plog.sets, plog.reps, plog.duration, plog.program_tag"
         * ;
         * public static final String MINIMUMARG =
         * "p.program_id, p.program_name, plog.day, e.exercise_name, plog.program_tag"
         * ;
         * public static final String MINDAYARG =
         * "p.program_id, p.program_name, plog.day, e.exercise_name, e.exercise_type";
         * public static final String ALLEXRCISEARG =
         * "p.program_id, p.program_name, plog.day, e.exercise_name, e.exercise_type, plog.sets, plog.reps, plog.duration"
         * ;
         */

        @Query(value = """
                        SELECT p.program_id, p.program_name, pl.plog_id, pl.day, e.exercise_name, e.etype,
                        pl.sets, pl.reps, pl.duration, pl.tag, p.pstatus
                        FROM workoutapp.program_log pl INNER JOIN workoutapp.programs p ON p.program_id = pl.program_id
                        INNER JOIN workoutapp.exercise_list e ON pl.exercise_id = e.exercise_id
                        WHERE p.program_name = :program_name AND p.user_id = :id""", nativeQuery = true)
        List<programDTO> findByProgramNamewithDetails(@Param("program_name") String program_name,
                        @Param("id") Long id);

        @Query(value = """
                        SELECT p.program_id, p.program_name, pl.day, pl.tag
                        FROM workoutapp.program_log pl INNER JOIN workoutapp.programs p ON p.program_id = pl.program_id
                        WHERE p.program_name = :program_name AND p.user_id = :id AND pl.day = :day LIMIT 1""", nativeQuery = true)
        programDTO findByProgramTag(@Param("program_name") String program_name, @Param("id") Long id,
                        @Param("day") String day);

        // ----------------------------------------------------------------------------------------------------------------------------------------------------
        // Insert section
        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE workoutapp.program_log pl
                        SET exercise_id = :exercise_id, day = :day, sets = :sets,
                        reps = :reps, duration = :duration, tag = :tag
                        FROM workoutapp.programs p INNER JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE p.program_id = :program_id AND u.id = :id AND pl.plog_id = :plog_id""", nativeQuery = true)
        int updateProgramLog(@Param("id") Long id, @Param("program_id") Long program_id, @Param("plog_id") Long plog_id,
                        @Param("exercise_id") Long exercise_id, @Param("day") String day,
                        @Param("sets") Integer sets, @Param("reps") Integer reps, @Param("duration") String duration,
                        @Param("tag") String tag);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO workoutapp.program_log (program_id, exercise_id, day, sets, reps, duration, tag)
                        SELECT p.program_id, :exercise_id, :day, :sets, :reps, :duration, :tag
                        FROM workoutapp.programs p INNER JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE p.program_id = :program_id AND u.id = :id""", nativeQuery = true)
        void insertProgramLog(@Param("id") Long id, @Param("program_id") Long program_id,
                        @Param("exercise_id") Long exercise_id,
                        @Param("day") String day,
                        @Param("sets") Integer sets, @Param("reps") Integer reps, @Param("duration") String duration,
                        @Param("tag") String tag);

        @Query(value = """
                        SELECT COALESCE(SUM(*),0)
                        FROM (SELECT DISTINCT d.logging_date FROM workoutapp.program_log pl
                        JOIN workoutapp.programs p ON pl.program_id = p.program_id JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE p.program_id = :program_id AND u.id = :user_id)""", nativeQuery = true)
        Long countByCurrentProgram(@Param("user_id") Long user_id, @Param("program_id") Long program_id);

}
