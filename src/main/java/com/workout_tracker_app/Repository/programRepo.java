package com.workout_tracker_app.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workout_tracker_app.Entity.Program;
import com.workout_tracker_app.dto.programDTO;

public interface programRepo extends JpaRepository<Program, Long> {

        public static final String TABLESELECT = " FROM Program p INNER JOIN p.users u";
        public static final String ORDER_BY = " ORDER BY p.program_status ASC";

        // Program Name section
        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p.program_status)"
                        + TABLESELECT
                        + " WHERE p.program_name = :program_name AND u.id = :id" + ORDER_BY)
        List<programDTO> findByProgramName(@Param("program_name") String program_name, @Param("id") String id);

        // Status section
        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p.program_status)"
                        + TABLESELECT
                        + " WHERE p.program_status = 'active' AND u.id = :id" + ORDER_BY + "LIMIT 1")
        programDTO findByActiveStatus(@Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id, p.program_name, p.program_status)"
                        + TABLESELECT
                        + " WHERE p.program_status = 'inactive' AND u.id = :id" + ORDER_BY)
        List<programDTO> findByInactiveStatus(@Param("id") String id);

        @Query(value = "SELECT new programDTO(p.program_id)"
                        + TABLESELECT
                        + " WHERE p.program_name = :program_name AND u.id = :id" + ORDER_BY)
        programDTO findProgramId(@Param("program_name") String program_name, @Param("id") String id);

}
