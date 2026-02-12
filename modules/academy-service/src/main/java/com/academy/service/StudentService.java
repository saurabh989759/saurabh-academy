package com.academy.service;

import com.academy.annotation.WithLock;
import com.academy.dto.StudentDTO;
import com.academy.entity.Batch;
import com.academy.entity.Student;
import com.academy.exception.BatchNotFoundException;
import com.academy.exception.StudentNotFoundException;
import com.academy.kafka.producer.StudentEventProducer;
import com.academy.mapper.StudentMapper;
import com.academy.repository.BatchRepository;
import com.academy.repository.MentorSessionRepository;
import com.academy.repository.StudentRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Student operations
 * Report: Feature Development Process - Service layer for student registration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final MentorSessionRepository mentorSessionRepository;
    private final StudentMapper studentMapper;
    private final StudentEventProducer eventProducer;
    
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "#batchId != null ? 'batch:' + #batchId : 'all'", 
               unless = "#result == null || #result.isEmpty()")
    public List<StudentDTO> getAllStudents(Long batchId) {
        List<Student> students = batchId != null 
            ? studentRepository.findByBatchId(batchId)
            : studentRepository.findAll();
        return students.stream()
            .map(studentMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<StudentDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
            .map(studentMapper::toDTO);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "student", key = "'student:' + #id", unless = "#result == null")
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));
        return studentMapper.toDTO(student);
    }
    
    @WithLock(key = "student:onboarding:#{#dto.email}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"student", "students"}, allEntries = true)
    public StudentDTO createStudent(StudentDTO dto) {
        log.info("Creating student: {}", dto.getEmail());
        
        // Check if student with email already exists (lock is automatically acquired by @WithLock)
        studentRepository.findByEmail(dto.getEmail())
            .ifPresent(existing -> {
                throw new IllegalStateException("Student with email " + dto.getEmail() + " already exists");
            });
        
        Student student = studentMapper.toEntity(dto);
        
        if (dto.getBatchId() != null) {
            // Use pessimistic lock for batch to prevent concurrent modifications
            Batch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new BatchNotFoundException(dto.getBatchId()));
            student.setBatch(batch);
        }
        
        Student saved = studentRepository.save(student);
        log.info("Student created with id: {}", saved.getId());
        
        // Report: Feature Development Process - Publish Kafka event after student registration
        eventProducer.publishStudentRegisteredEvent(saved);
        
        return studentMapper.toDTO(saved);
    }
    
    @WithLock(key = "student:update:#{#id}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"student", "students"}, key = "'student:' + #id", allEntries = true)
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        // Use pessimistic lock to prevent concurrent updates
        Student student = studentRepository.findByIdWithLock(id)
            .orElseThrow(() -> new StudentNotFoundException(id));
        
        // If email is being changed, check for duplicates
        if (!student.getEmail().equals(dto.getEmail())) {
            // Additional lock for email change (nested lock scenario)
            studentRepository.findByEmail(dto.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalStateException("Student with email " + dto.getEmail() + " already exists");
                    }
                });
        }
        
        return performUpdate(student, dto);
    }
    
    private StudentDTO performUpdate(Student student, StudentDTO dto) {
        student.setName(dto.getName());
        student.setGraduationYear(dto.getGraduationYear());
        student.setUniversityName(dto.getUniversityName());
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setBuddyId(dto.getBuddyId());
        
        if (dto.getBatchId() != null) {
            // Use pessimistic lock for batch
            Batch batch = batchRepository.findByIdWithLock(dto.getBatchId())
                .orElseThrow(() -> new BatchNotFoundException(dto.getBatchId()));
            student.setBatch(batch);
        }
        
        Student updated = studentRepository.save(student);
        return studentMapper.toDTO(updated);
    }
    
    @Transactional
    @CacheEvict(value = {"student", "students", "mentorSession", "mentorSessions"}, key = "'student:' + #id", allEntries = true)
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));
        
        // Delete related mentor sessions first to avoid foreign key constraint violation
        List<com.academy.entity.MentorSession> sessions = mentorSessionRepository.findByStudentId(id);
        if (!sessions.isEmpty()) {
            log.info("Deleting {} mentor session(s) for student {}", sessions.size(), id);
            mentorSessionRepository.deleteByStudentId(id);
        }
        
        // Delete the student
        studentRepository.deleteById(id);
        log.info("Student deleted with id: {}", id);
    }
}

