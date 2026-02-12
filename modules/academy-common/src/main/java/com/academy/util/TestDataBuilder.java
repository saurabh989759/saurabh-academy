package com.academy.util;

import com.academy.dto.*;
import com.academy.entity.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.util.Date;

/**
 * Test data builders for creating test objects
 * Ensures consistent test data across all test classes
 */
public class TestDataBuilder {
    
    // Student DTO Builder
    public static class StudentDTOBuilder {
        private Long id;
        private String name = "Test Student";
        private Integer graduationYear = 2024;
        private String universityName = "Test University";
        private String email = "test@example.com";
        private String phoneNumber = "123-456-7890";
        private Long batchId = 1L;
        private Long buddyId;
        
        public StudentDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public StudentDTOBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public StudentDTOBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public StudentDTOBuilder batchId(Long batchId) {
            this.batchId = batchId;
            return this;
        }
        
        public StudentDTO build() {
            StudentDTO dto = new StudentDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setGraduationYear(graduationYear);
            dto.setUniversityName(universityName);
            dto.setEmail(email);
            dto.setPhoneNumber(phoneNumber);
            dto.setBatchId(batchId);
            dto.setBuddyId(buddyId);
            return dto;
        }
    }
    
    public static StudentDTOBuilder studentDTO() {
        return new StudentDTOBuilder();
    }
    
    // Batch DTO Builder
    public static class BatchDTOBuilder {
        private Long id;
        private String name = "Test Batch";
        private LocalDate startMonth = LocalDate.now();
        private String currentInstructor = "Test Instructor";
        private Long batchTypeId = 1L;
        private java.util.Set<Long> classIds;
        
        public BatchDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public BatchDTOBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public BatchDTOBuilder batchTypeId(Long batchTypeId) {
            this.batchTypeId = batchTypeId;
            return this;
        }
        
        public BatchDTO build() {
            BatchDTO dto = new BatchDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setStartMonth(startMonth);
            dto.setCurrentInstructor(currentInstructor);
            dto.setBatchTypeId(batchTypeId);
            dto.setClassIds(classIds);
            return dto;
        }
    }
    
    public static BatchDTOBuilder batchDTO() {
        return new BatchDTOBuilder();
    }
    
    // Class DTO Builder
    public static class ClassDTOBuilder {
        private Long id;
        private String name = "Test Class";
        private LocalDate date = LocalDate.now();
        private LocalTime time = LocalTime.of(10, 0);
        private String instructor = "Test Instructor";
        
        public ClassDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public ClassDTOBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public ClassDTO build() {
            ClassDTO dto = new ClassDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setDate(date);
            dto.setTime(time);
            dto.setInstructor(instructor);
            return dto;
        }
    }
    
    public static ClassDTOBuilder classDTO() {
        return new ClassDTOBuilder();
    }
    
    // Mentor DTO Builder
    public static class MentorDTOBuilder {
        private Long id;
        private String name = "Test Mentor";
        private String currentCompany = "Test Company";
        
        public MentorDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public MentorDTOBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public MentorDTO build() {
            MentorDTO dto = new MentorDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setCurrentCompany(currentCompany);
            return dto;
        }
    }
    
    public static MentorDTOBuilder mentorDTO() {
        return new MentorDTOBuilder();
    }
    
    // MentorSession DTO Builder
    public static class MentorSessionDTOBuilder {
        private Long id;
        private Instant time = Instant.now();
        private Integer durationMinutes = 60;
        private Long studentId = 1L;
        private Long mentorId = 1L;
        private Integer studentRating;
        private Integer mentorRating;
        
        public MentorSessionDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public MentorSessionDTOBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }
        
        public MentorSessionDTOBuilder mentorId(Long mentorId) {
            this.mentorId = mentorId;
            return this;
        }
        
        public MentorSessionDTOBuilder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }
        
        public MentorSessionDTO build() {
            MentorSessionDTO dto = new MentorSessionDTO();
            dto.setId(id);
            dto.setTime(time);
            dto.setDurationMinutes(durationMinutes);
            dto.setStudentId(studentId);
            dto.setMentorId(mentorId);
            dto.setStudentRating(studentRating);
            dto.setMentorRating(mentorRating);
            return dto;
        }
    }
    
    public static MentorSessionDTOBuilder mentorSessionDTO() {
        return new MentorSessionDTOBuilder();
    }
    
    // Entity Builders
    public static Student studentEntity(Long id, String email) {
        Student student = new Student();
        student.setId(id);
        student.setName("Test Student");
        student.setEmail(email);
        student.setGraduationYear(2024);
        student.setUniversityName("Test University");
        student.setPhoneNumber("123-456-7890");
        student.setVersion(0L);
        return student;
    }
    
    public static Batch batchEntity(Long id, String name) {
        Batch batch = new Batch();
        batch.setId(id);
        batch.setName(name);
        batch.setStartMonth(LocalDate.now());
        batch.setCurrentInstructor("Test Instructor");
        batch.setVersion(0L);
        return batch;
    }
    
    public static BatchType batchTypeEntity(Long id, String name) {
        BatchType batchType = new BatchType();
        batchType.setId(id);
        batchType.setName(name);
        return batchType;
    }
    
    public static ClassEntity classEntity(Long id, String name) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setId(id);
        classEntity.setName(name);
        classEntity.setDate(LocalDate.now());
        classEntity.setTime(LocalTime.of(10, 0));
        classEntity.setInstructor("Test Instructor");
        return classEntity;
    }
    
    public static Mentor mentorEntity(Long id, String name) {
        Mentor mentor = new Mentor();
        mentor.setId(id);
        mentor.setName(name);
        mentor.setCurrentCompany("Test Company");
        return mentor;
    }
    
    public static MentorSession mentorSessionEntity(Long id, Student student, Mentor mentor) {
        MentorSession session = new MentorSession();
        session.setId(id);
        session.setTime(java.sql.Timestamp.from(Instant.now()));
        session.setDurationMinutes(60);
        session.setStudent(student);
        session.setMentor(mentor);
        session.setVersion(0L);
        return session;
    }
}

