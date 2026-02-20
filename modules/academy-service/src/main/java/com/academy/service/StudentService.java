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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<StudentDTO> getAllStudents(Long batchId) {
        var records = (batchId != null)
            ? studentRepository.findByBatchId(batchId)
            : studentRepository.findAll();
        return records.stream().map(studentMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public Page<StudentDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(studentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "student", key = "'student:' + #id", unless = "#result == null")
    public StudentDTO getStudentById(Long id) {
        return studentMapper.toDTO(fetchStudentOrThrow(id));
    }

    @WithLock(key = "student:onboarding:#{#request.email}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"student", "students"}, allEntries = true)
    public StudentDTO createStudent(StudentDTO request) {
        log.info("Registering new student with email: {}", request.getEmail());

        studentRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new IllegalStateException("A student with email " + request.getEmail() + " already exists");
        });

        Student newStudent = studentMapper.toEntity(request);
        assignBatchIfPresent(newStudent, request.getBatchId());

        Student persisted = studentRepository.save(newStudent);
        log.info("Student registered successfully, assigned id: {}", persisted.getId());
        eventProducer.publishStudentRegisteredEvent(persisted);

        return studentMapper.toDTO(persisted);
    }

    @WithLock(key = "student:update:#{#id}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"student", "students"}, key = "'student:' + #id", allEntries = true)
    public StudentDTO updateStudent(Long id, StudentDTO request) {
        Student existing = studentRepository.findByIdWithLock(id)
            .orElseThrow(() -> new StudentNotFoundException(id));

        if (!existing.getEmail().equals(request.getEmail())) {
            studentRepository.findByEmail(request.getEmail()).ifPresent(conflict -> {
                if (!conflict.getId().equals(id)) {
                    throw new IllegalStateException("Email " + request.getEmail() + " is already taken");
                }
            });
        }

        return applyUpdatesAndSave(existing, request);
    }

    @Transactional
    @CacheEvict(value = {"student", "students", "mentorSession", "mentorSessions"}, key = "'student:' + #id", allEntries = true)
    public void deleteStudent(Long id) {
        fetchStudentOrThrow(id);

        var linkedSessions = mentorSessionRepository.findByStudentId(id);
        if (!linkedSessions.isEmpty()) {
            log.info("Removing {} session(s) linked to student {}", linkedSessions.size(), id);
            mentorSessionRepository.deleteByStudentId(id);
        }

        studentRepository.deleteById(id);
        log.info("Student {} removed from the system", id);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Student fetchStudentOrThrow(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    private void assignBatchIfPresent(Student student, Long batchId) {
        if (batchId != null) {
            Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException(batchId));
            student.setBatch(batch);
        }
    }

    private StudentDTO applyUpdatesAndSave(Student student, StudentDTO request) {
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setUniversityName(request.getUniversityName());
        student.setGraduationYear(request.getGraduationYear());
        student.setBuddyId(request.getBuddyId());

        if (request.getBatchId() != null) {
            Batch batch = batchRepository.findByIdWithLock(request.getBatchId())
                .orElseThrow(() -> new BatchNotFoundException(request.getBatchId()));
            student.setBatch(batch);
        }

        return studentMapper.toDTO(studentRepository.save(student));
    }
}
