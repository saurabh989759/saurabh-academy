package com.academy.service;

import com.academy.dto.StudentDTO;
import com.academy.entity.Batch;
import com.academy.entity.Student;
import com.academy.exception.BatchNotFoundException;
import com.academy.exception.StudentNotFoundException;
import com.academy.kafka.StudentEventProducer;
import com.academy.mapper.StudentMapper;
import com.academy.repository.BatchRepository;
import com.academy.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Transactional
    @CacheEvict(value = {"student", "students"}, allEntries = true)
    public StudentDTO createStudent(StudentDTO dto) {
        log.info("Creating student: {}", dto.getEmail());
        
        Student student = studentMapper.toEntity(dto);
        
        if (dto.getBatchId() != null) {
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
    
    @Transactional
    @CacheEvict(value = {"student", "students"}, key = "'student:' + #id", allEntries = true)
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));
        
        student.setName(dto.getName());
        student.setGraduationYear(dto.getGraduationYear());
        student.setUniversityName(dto.getUniversityName());
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setBuddyId(dto.getBuddyId());
        
        if (dto.getBatchId() != null) {
            Batch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new BatchNotFoundException(dto.getBatchId()));
            student.setBatch(batch);
        }
        
        Student updated = studentRepository.save(student);
        return studentMapper.toDTO(updated);
    }
    
    @Transactional
    @CacheEvict(value = {"student", "students"}, key = "'student:' + #id", allEntries = true)
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
        log.info("Student deleted with id: {}", id);
    }
}

