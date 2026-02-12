-- Initialize schema for academy_db_dev on first run
-- This ensures tables exist even if Flyway hasn't run yet
-- Note: Flyway will handle migrations when the application starts

USE academy_db_dev;

-- Only create tables if they don't exist (idempotent)
-- This script runs on container initialization

-- Create batch_type table
CREATE TABLE IF NOT EXISTS batch_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    INDEX idx_batch_type_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create batches table
CREATE TABLE IF NOT EXISTS batches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_month DATE NOT NULL,
    current_instructor VARCHAR(255) NOT NULL,
    batch_type_id BIGINT NOT NULL,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (batch_type_id) REFERENCES batch_type(id),
    INDEX idx_batches_batch_type (batch_type_id),
    INDEX idx_batches_start_month (start_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create classes table
CREATE TABLE IF NOT EXISTS classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    instructor VARCHAR(255) NOT NULL,
    INDEX idx_classes_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create batches_classes join table
CREATE TABLE IF NOT EXISTS batches_classes (
    batch_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    PRIMARY KEY (batch_id, class_id),
    FOREIGN KEY (batch_id) REFERENCES batches(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create students table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    graduation_year INT,
    university_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    batch_id BIGINT,
    buddy_id BIGINT,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    INDEX idx_students_batch (batch_id),
    INDEX idx_students_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create mentors table
CREATE TABLE IF NOT EXISTS mentors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_company VARCHAR(255),
    version BIGINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create mentor_sessions table
CREATE TABLE IF NOT EXISTS mentor_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL,
    student_id BIGINT NOT NULL,
    mentor_id BIGINT NOT NULL,
    student_rating INT,
    mentor_rating INT,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (mentor_id) REFERENCES mentors(id),
    INDEX idx_sessions_student (student_id),
    INDEX idx_sessions_mentor (mentor_id),
    INDEX idx_sessions_time (time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create student_batch_history table
CREATE TABLE IF NOT EXISTS student_batch_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    shift_date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    INDEX idx_history_student (student_id),
    INDEX idx_history_batch (batch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create audit_events table for Kafka event storage
CREATE TABLE IF NOT EXISTS audit_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_audit_event_type (event_type),
    INDEX idx_audit_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create users table for JWT authentication (if not exists)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Flyway schema history table (if not exists)
CREATE TABLE IF NOT EXISTS flyway_schema_history (
    installed_rank INT NOT NULL,
    version VARCHAR(50),
    description VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    script VARCHAR(1000) NOT NULL,
    checksum INT,
    installed_by VARCHAR(100) NOT NULL,
    installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_time INT NOT NULL,
    success BOOLEAN NOT NULL,
    PRIMARY KEY (installed_rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

