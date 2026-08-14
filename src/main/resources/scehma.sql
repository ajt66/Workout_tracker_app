CREATE SCHEMA IF NOT EXISTS workoutapp;

CREATE TABLE IF NOT EXISTS workoutapp.User {
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
};

CREATE TABLE IF NOT EXISTS workoutapp.ExerciseList (
    exerciseId BIGINT AUTO_INCREMENT PRIMARY KEY,
    exercise_name VARCHAR(50) NOT NULL UNIQUE,
    type ENUM('ISOLATION', 'COMPOUND'),
    muscle_group VARCHAR(50),
    category ENUM("CARDIO", "STRENGTH", "FLEXIBILTY","HYPERTROPHY","ISOMETRICS") NOT NULL
);

CREATE TABLE IF NOT EXISTS workoutapp.DailyLog {
    logId BIGINT AUTO_INCREMENT PRIMARY KEY,
    day VARCHAR(50) NOT NULL,
    logging_date DATE DEFAULT CURRENT_DATE NOT NULL,
    exercise_id BIGINT NOT NULL,
    weights DECIMAL(10,2),
    sets DECIMAL(10,2),
    reps DECIMAL(10,2),
    duration VARCHAR(50),
    comment VARCHAR(50),
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING' NOT NULL
    CONSTRAINT fk_exerciseList_id1
    FOREIGN KEY (Exercise_ID) REFERENCES ExerciseList(exerciseId)
};

CREATE TABLE IF NOT EXISTS workoutapp.Program {
    programId BIGINT AUTO_INCREMENT PRIMARY KEY,
    program_name VARCHAR(50) NOT NULL,
    exercise_id BIGINT NOT NULL,
    day VARCHAR(50) NOT NULL,
    sets DECIMAL(10,2),
    reps DECIMAL(10,2),
    duration VARCHAR(50),
    tag ENUM('PUSH', 'PULL', 'LEGS', 'FULL_BODY', 'UPPER', 'LOWER', 'BRO_SPLIT', 'REST', 'CHEST', 'ARMS', 'BACK', 'SHOULDERS',
        'CARDIO', 'CORE', 'STRETCHING') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL
    CONSTRAINT fk_exerciseList_id2
    FOREIGN KEY (exercise_iD) REFERENCES ExerciseList(exerciseId)
};