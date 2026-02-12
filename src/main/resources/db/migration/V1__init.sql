-- Report: DB Schema Design - Initial database schema and sample data
-- Flyway migration script for Academy Backend

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
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    INDEX idx_students_batch (batch_id),
    INDEX idx_students_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create mentors table
CREATE TABLE IF NOT EXISTS mentors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_company VARCHAR(255)
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

-- Create users table for JWT authentication
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample data: Batch Types
INSERT INTO batch_type (name) VALUES
    ('Full Stack Development'),
    ('Data Science'),
    ('Machine Learning')
ON DUPLICATE KEY UPDATE name=name;

-- Insert sample data: Batches
INSERT INTO batches (name, start_month, current_instructor, batch_type_id) VALUES
    ('FSD-2024-01', '2024-01-01', 'John Instructor', 1),
    ('DS-2024-02', '2024-02-01', 'Jane Instructor', 2)
ON DUPLICATE KEY UPDATE name=name;

-- Insert sample data: Classes
INSERT INTO classes (name, date, time, instructor) VALUES
    ('Introduction to Spring Boot', '2024-01-15', '10:00:00', 'John Instructor'),
    ('Database Design', '2024-01-20', '14:00:00', 'John Instructor'),
    ('Kafka Basics', '2024-01-25', '10:00:00', 'John Instructor')
ON DUPLICATE KEY UPDATE name=name;

-- Link classes to batches
INSERT INTO batches_classes (batch_id, class_id) VALUES
    (1, 1),
    (1, 2),
    (1, 3)
ON DUPLICATE KEY UPDATE batch_id=batch_id;

-- Insert sample data: Students
INSERT INTO students (name, graduation_year, university_name, email, phone_number, batch_id, buddy_id) VALUES
    ('Alice Smith', 2023, 'State University', 'alice@example.com', '123-456-7890', 1, NULL),
    ('Bob Johnson', 2022, 'Tech University', 'bob@example.com', '234-567-8901', 1, 1),
    ('Charlie Brown', 2024, 'State University', 'charlie@example.com', '345-678-9012', 2, NULL),
    ('Diana Prince', 2023, 'Tech University', 'diana@example.com', '456-789-0123', 2, 3)
ON DUPLICATE KEY UPDATE email=email;

-- Insert sample data: Mentors
INSERT INTO mentors (name, current_company) VALUES
    ('Mentor One', 'Google'),
    ('Mentor Two', 'Microsoft')
ON DUPLICATE KEY UPDATE name=name;

-- Insert sample data: Mentor Sessions
INSERT INTO mentor_sessions (time, duration_minutes, student_id, mentor_id, student_rating, mentor_rating) VALUES
    ('2024-01-10 15:00:00', 60, 1, 1, 5, 4),
    ('2024-01-12 16:00:00', 45, 2, 2, 5, 5)
ON DUPLICATE KEY UPDATE time=time;

-- Insert default users for JWT authentication
-- Password: password123 (BCrypt encoded)
-- Generated using: BCryptPasswordEncoder.encode("password123")
-- Note: BCrypt generates different hashes each time, but all are valid for the same password
INSERT INTO users (username, password, role, enabled) VALUES
    ('admin@academy.com', '$2a$10$skaMk5mvUYNK88SyP3EQbumduSxI1a/mrWdCCaXmMZo6XbTs7uFRy', 'ROLE_USER', TRUE),
    ('user@academy.com', '$2a$10$skaMk5mvUYNK88SyP3EQbumduSxI1a/mrWdCCaXmMZo6XbTs7uFRy', 'ROLE_USER', TRUE)
ON DUPLICATE KEY UPDATE username=username;

