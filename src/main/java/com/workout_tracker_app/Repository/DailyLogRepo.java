package com.workout_tracker_app.Repository;

import com.workout_tracker_app.Entity.DailyLog;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DailyLogRepo extends JpaRepository<DailyLog, Long> {

}
