package com.workout_tracker_app.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workout_tracker_app.Entity.Program;
import com.workout_tracker_app.Entity.programLog;
import com.workout_tracker_app.dto.programDTO;
import com.workout_tracker_app.Entity.ExerciseList.Type;
import com.workout_tracker_app.Entity.programLog.Tag;

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

        @Query(value = "SELECT new com.workout_tracker_app.dto.programDTO(p.program_id, p.program_name, plog.day, e.exercise_name, e.exercise_type, plog.sets, plog.reps, plog.duration, plog.program_tag)"
                        + TABLESELECT
                        + " WHERE p.program_name = :program_name AND u.id = :id" + ORDER_BY)
        List<programDTO> findByProgramNamewithDetails(@Param("program_name") String program_name,
                        @Param("id") Long id);

        // ----------------------------------------------------------------------------------------------------------------------------------------------------
        // Insert section
        @Modifying
        @Query(value = """
                        UPDATE workoutapp.program_log pl
                        SET pl.exercise_id = :exercise_id, pl.day = :day, pl.sets = :sets,
                        pl.reps = :reps, pl.duration = :duration
                        FROM workoutapp.programs p INNER JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE p.program_id = :program_id AND u.id = :id""", nativeQuery = true)
        int updateProgramLog(@Param("program_id") Long program_id, @Param("exercise_id") Long exercise_id,
                        @Param("day") String day,
                        @Param("sets") Integer sets, @Param("reps") Integer reps, @Param("duration") String duration,
                        @Param("id") Long id);

        @Modifying
        @Query(value = """
                        INSERT INTO workoutapp.program_log (program_id, exercise_id, day, sets, reps, duration, tag)
                        SELECT p.program_id, :exercise_id, :day, :sets, :reps, :duration, :tag
                        FROM workoutapp.programs p INNER JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE p.program_id = :program_id AND u.id = :id""", nativeQuery = true)
        void insertProgramLog(@Param("program_id") Long program_id, @Param("exercise_id") Long exercise_id,
                        @Param("day") String day,
                        @Param("sets") Integer sets, @Param("reps") Integer reps, @Param("duration") String duration,
                        @Param("tag") Tag tag, @Param("id") Long id);

}
