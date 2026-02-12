-- Insert sample data (idempotent - uses ON DUPLICATE KEY UPDATE)
-- This ensures data exists but won't fail if already present

USE academy_db_dev;

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

-- Insert default users for JWT authentication (if not exists)
-- Password: password123 (BCrypt encoded - use /api/dev/generate-hash to get current hash)
INSERT INTO users (username, password, role, enabled) VALUES
    ('admin@academy.com', '$2a$10$ijQ9gDLabcX0PAJ3LdPlqeNm2ahqpkIWvMn9muKow3IKNMZJKHRAK', 'ROLE_USER', TRUE),
    ('user@academy.com', '$2a$10$ijQ9gDLabcX0PAJ3LdPlqeNm2ahqpkIWvMn9muKow3IKNMZJKHRAK', 'ROLE_USER', TRUE)
ON DUPLICATE KEY UPDATE username=username;

