package com.workout_tracker_app.Repository;

import com.workout_tracker_app.Entity.Program;
import com.workout_tracker_app.dto.programDTO;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface programRepo extends JpaRepository<Program, Long> {

    // Program Name section
    @Query(value = "SELECT new programDTO(p.programName, p.day, e.name, e.type, p.status)"
            + " FROM Program p JOIN ExerciseList e ON p.programId = e.programs.programId"
            + " WHERE p.programName = :programName")
    List<programDTO> findByProgramName(@Param("programName") String programName);

    @Query(value = "SELECT new programDTO(p.programName, p.day, e.name, e.type, p.sets, p.reps, p.duration, p.tag, p.status)"
            + " FROM Program p JOIN ExerciseList e ON p.programId = e.programs.programId"
            + " WHERE p.programName = :programName")
    List<programDTO> findByProgramNamewithDetails(@Param("programName") String programName);

    // Day section
    @Query(value = "SELECT new programDTO(p.programName, p.day, e.name, e.type, p.status)"
            + " FROM Program p JOIN ExerciseList e ON p.programId = e.programs.programId"
            + " WHERE p.day = :day")
    List<programDTO> findByDay(@Param("day") String day);

    @Query(value = "SELECT new programDTO(p.programName, p.day, e.name, e.type, p.sets, p.reps, p.duration, p.tag, p.status)"
            + " FROM Program p JOIN ExerciseList e ON p.programId = e.programs.programId"
            + " WHERE p.day = :day")
    List<programDTO> findByDaywithDetails(@Param("day") String day);

    // Exercise Name section
    @Query(value = "SELECT new programDTO(p.programName, p.day, e.name, e.type, p.status)"
            + " FROM Program p JOIN ExerciseList e ON p.programId = e.programs.programId"
            + " WHERE e.name = :exerciseName")
    List<programDTO> findByExerciseName(@Param("exerciseName") String exerciseName);

    @Query(value = "SELECT new programDTO(p.programName, p.day, e.name, e.type, p.sets,p.reps,p.duration,p.tag,p.status)"
            + " FROM Program p JOIN ExerciseList e ON p.programId = e.programs.programId"
            + " WHERE e.name = :exerciseName")
    List<programDTO> findByExerciseNamewithDetails(@Param("exerciseName") String exerciseName);

    // Status section
    @Query(value = "SELECT new programDTO(p.programName, p.day, e.name, e.type, p.status)"
            + " FROM Program p JOIN ExerciseList e ON p.programId = e.programs.programId"
            + " WHERE p.status = :status")
    List<programDTO> findByStatus(@Param("status") String status);

    @Query(value = "SELECT new programDTO(p.programName, p.day, e.name, e.type, p.sets,p.reps,p.duration,p.tag,p.status)"
            + " FROM Program p JOIN ExerciseList e ON p.programId = e.programs.programId"
            + " WHERE p.status = :status")
    List<programDTO> findByStatuswithDetails(@Param("status") String status);

}
