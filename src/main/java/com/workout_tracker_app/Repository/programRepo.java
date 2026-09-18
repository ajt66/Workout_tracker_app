package com.workout_tracker_app.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workout_tracker_app.Entity.Program;
import com.workout_tracker_app.dto.programDTO;
import com.workout_tracker_app.Entity.Program.Status;

public interface programRepo extends JpaRepository<Program, Long> {

        public static final String TABLESELECT = " FROM Program p INNER JOIN p.users u";
        public static final String ORDER_BY = " ORDER BY p.program_status ASC";

        // Program Name section
        @Query(value = "SELECT new com.workout_tracker_app.dto.programDTO(p.program_id, p.program_name, p.program_status)"
                        + TABLESELECT
                        + " WHERE p.program_name = :program_name AND u.id = :id" + ORDER_BY)
        List<programDTO> findByProgramName(@Param("program_name") String program_name, @Param("id") Long id);

        // Status section
        @Query(value = "SELECT new com.workout_tracker_app.dto.programDTO(p.program_id, p.program_name, p.program_status)"
                        + TABLESELECT
                        + " WHERE p.program_status = :status AND u.id = :id" + ORDER_BY
                        + " LIMIT 1")
        programDTO findByActiveStatus(@Param("status") Status status, @Param("id") Long id);

        @Query(value = "SELECT new com.workout_tracker_app.dto.programDTO(p.program_id, p.program_name, p.program_status)"
                        + TABLESELECT
                        + " WHERE p.program_status = :status AND u.id = :id" + ORDER_BY)
        List<programDTO> findByInactiveStatus(@Param("status") Status status, @Param("id") Long id);

        @Query(value = "SELECT new com.workout_tracker_app.dto.programDTO(p.program_id)"
                        + TABLESELECT
                        + " WHERE p.program_name = :program_name AND u.id = :id" + ORDER_BY)
        programDTO findProgramId(@Param("program_name") String program_name, @Param("id") Long id);

        // ---------------------------------------------------------------------------------------------------------------------------------------------------
        @Modifying
        @Query(value = """
                        UPDATE p SET p.program_name = :program_name
                        FROM workoutapp.programs p INNER JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE program_id = :p_id AND u.id = :id""", nativeQuery = true)
        Long updateProgramName(@Param("program_name") String program_name, @Param("p_id") Long p_id,
                        @Param("id") Long id);

        @Modifying
        @Query(value = """
                        UPDATE workoutapp.programs SET p.pstatus =:pstatus
                        FROM workoutapp.programs p INNER JOIN workoutapp.users u ON p.user_id = u.id
                        WHERE program_id = :p_id AND u.id = :id""", nativeQuery = true)
        Long updateProgramStatus(@Param("pstatus") Status pstatus, @Param("p_id") Long p_id, @Param("id") Long id);

        @Modifying
        @Query(value = """
                        INSERT INTO workoutapp.programs (program_name, user_id, pstatus)
                        SELECT :program_name, u.id, 'inactive'
                        FROM workoutapp.users u
                        WHERE u.id = :id""", nativeQuery = true)
        void insertProgram(@Param("program_name") String program_name, @Param("id") Long id);

}