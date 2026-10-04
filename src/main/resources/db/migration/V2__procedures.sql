CREATE OR REPLACE PROCEDURE copy_program_data(u_id BIGINT)
LANGUAGE plpgsql
AS $$
    DECLARE 
        p_id BIGINT;
        W_day TEXT;
    BEGIN
        W_day = TO_CHAR(CURRENT_DATE, 'FMDay');
        SELECT program_id INTO p_id FROM workoutapp.programs WHERE user_id = u_id AND pstatus = 'active' LIMIT 1;

        INSERT INTO workoutapp.daily_log (program_id, day, logging_date, exercise_id, sets, reps, duration, dstatus)
        SELECT p.program_id, p.day, CURRENT_DATE, p.exercise_id, p.sets, p.reps, p.duration, 'pending'
        FROM workoutapp.program_log p WHERE p.program_id = p_id AND p.day = W_Day;       
    END;
$$;

CREATE OR REPLACE PROCEDURE change_status(u_id BIGINT)
LANGUAGE plpgsql
AS $$
    DECLARE 
        p_id BIGINT;
    BEGIN
        SELECT program_id INTO p_id FROM workoutapp.programs WHERE user_id = u_id AND pstatus = 'active' LIMIT 1;

        UPDATE workoutapp.daily_log d SET dstatus = 'cancelled' FROM workoutapp.programs p
        WHERE p.program_id = p_id AND d.dstatus = 'pending' AND d.logging_date < CURRENT_DATE;
    END;    
$$;