package com.workout_tracker_app.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workout_tracker_app.Entity.Program;
import com.workout_tracker_app.Entity.programLog;
import com.workout_tracker_app.dto.programDTO;

public interface programLogRepo extends JpaRepository<programLog, Long> {

        public static final String TABLESELECT = " FROM programLog p_log INNER JOIN p_log.exercise_id e"
                        + " INNER JOIN p_log.program.users u" + " INNER JOIN p_log.program p";
        public static final String ORDER_BY = " ORDER BY CASE p.day"
                        + " WHEN 'monday' THEN 1"
                        + " WHEN 'tuesday' THEN 2"
                        + " WHEN 'wednesday' THEN 3"
                        + " WHEN 'thursday' THEN 4"
                        + " WHEN 'friday' THEN 5"
                        + " WHEN 'saturday' THEN 6"
                        + " WHEN 'sunday' THEN 7"
                        + " END ASC";
        /*
         * public static final String ALLPROGRAMARG =
         * "p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration, p_log.program_tag"
         * ;
         * public static final String MINIMUMARG =
         * "p.program_id, p.program_name, p_log.day, e.exercise_name, p_log.program_tag"
         * ;
         * public static final String MINDAYARG =
         * "p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type";
         * public static final String ALLEXRCISEARG =
         * "p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration"
         * ;
         */

        @Query(value = "SELECT new programDTO('p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration, p_log.program_tag')"
                        + TABLESELECT
                        + " WHERE p_log.day = :day AND p.program_name =:program_name AND u.id = :id" + ORDER_BY)
        List<programDTO> findByProgramNamewithDetails(@Param("program_name") String program_name,
                        @Param("id") String id);

        // Day section
        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, p_log.program_tag)"
                        + TABLESELECT
                        + " WHERE p_log.day BETWEEN :day1 AND :day2 AND u.id = :id" + ORDER_BY)
        List<programDTO> findDaysBetween(@Param("day1") String day1, @Param("day2") String day2,
                        @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration, p_log.program_tag)"
                        + TABLESELECT
                        + " WHERE p_log.day BETWEEN :day1 AND :day2 AND u.id = :id" + ORDER_BY)
        List<programDTO> findDaysBetweenWithDetails(@Param("day1") String day1, @Param("day2") String day2,
                        @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, p_log.program_tag)"
                        + TABLESELECT
                        + " WHERE p_log.day BETWEEN :day1 AND :day2 AND p.program_name =:program_name AND u.id = :id"
                        + ORDER_BY)
        programDTO findByDaysBetweenAndProgram(@Param("day1") String day1, @Param("day2") String day2,
                        @Param("program_name") String program_name,
                        @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration, p_log.program_tag)"
                        + TABLESELECT
                        + " WHERE p_log.day BETWEEN :day1 AND :day2 AND p.program_name =:program_name AND u.id = :id"
                        + ORDER_BY)
        List<programDTO> findByDaysBetweenAndProgramwithDetails(@Param("day1") String day1, @Param("day2") String day2,
                        @Param("program_name") String program_name, @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, p_log.program_tag)"
                        + TABLESELECT
                        + " WHERE p_log.day = :day AND p.program_name =:program_name AND u.id = :id" + ORDER_BY)
        programDTO findByDayAndProgram(@Param("day") String day, @Param("program_name") String program_name,
                        @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration, p_log.program_tag)"
                        + TABLESELECT
                        + " WHERE p_log.day = :day AND p.program_name =:program_name AND u.id = :id" + ORDER_BY)
        List<programDTO> findByDayAndProgramwithDetails(@Param("day") String day,
                        @Param("program_name") String program_name, @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type)"
                        + TABLESELECT
                        + " WHERE p_log.day = :day AND u.id = :id" + ORDER_BY)
        List<programDTO> findByDay(@Param("day") String day, @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration, p_log.program_tag)"
                        + TABLESELECT
                        + " WHERE p_log.day = :day AND u.id = :id" + ORDER_BY)
        List<programDTO> findByDaywithDetails(@Param("day") String day, @Param("id") String id);

        // Exercise Name section
        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type)"
                        + TABLESELECT
                        + " WHERE e.exercise_name = :exerciseName AND u.id = :id" + ORDER_BY)
        List<programDTO> findByExerciseName(@Param("exerciseName") String exerciseName, @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration)"
                        + TABLESELECT
                        + " WHERE e.exercise_name = :exerciseName AND u.id = :id" + ORDER_BY)
        List<programDTO> findByExerciseNameWithDetails(@Param("exerciseName") String exerciseName,
                        @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type)"
                        + TABLESELECT
                        + " WHERE e.exercise_name = :exerciseName AND p.program_name =:program_name AND u.id = :id"
                        + ORDER_BY)
        programDTO findByExerciseNameAndProgram(@Param("exerciseName") String exerciseName,
                        @Param("program_name") String program_name,
                        @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration)"
                        + TABLESELECT
                        + " WHERE e.exercise_name = :exerciseName AND u.id = :id" + ORDER_BY)
        List<programDTO> findByExerciseNameAndProgramWithDetails(@Param("exerciseName") String exerciseName,
                        @Param("program_name") String program_name,
                        @Param("id") String id);

        // Exercise Type section

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type)"
                        + TABLESELECT
                        + " WHERE e.exercise_type = :exerciseType AND u.id = :id" + ORDER_BY)
        List<programDTO> findByExerciseType(@Param("exerciseType") String exerciseType, @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration)"
                        + TABLESELECT
                        + " WHERE e.exercise_type = :exerciseType AND u.id = :id" + ORDER_BY)
        List<programDTO> findByExerciseTypewithDetails(@Param("exerciseType") String exerciseType,
                        @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type)"
                        + TABLESELECT
                        + " WHERE e.exercise_type = :exerciseType AND p.program_name =:program_name AND u.id = :id"
                        + ORDER_BY)
        programDTO findByExerciseTypeAndProgram(@Param("exerciseType") String exerciseType,
                        @Param("program_name") String program_name,
                        @Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p_log.day, e.exercise_name, e.exercise_type, p_log.sets, p_log.reps, p_log.duration)"
                        + TABLESELECT
                        + " WHERE e.exercise_type = :exerciseType AND u.id = :id" + ORDER_BY)
        List<programDTO> findByExerciseTypeAndProgramWithDetails(@Param("exerciseType") String exerciseType,
                        @Param("program_name") String program_name,
                        @Param("id") String id);

}
