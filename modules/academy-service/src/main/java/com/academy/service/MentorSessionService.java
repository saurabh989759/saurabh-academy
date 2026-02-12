package com.academy.service;

import com.academy.dto.MentorSessionDTO;
import com.academy.entity.Mentor;
import com.academy.entity.MentorSession;
import com.academy.entity.Student;
import com.academy.exception.MentorNotFoundException;
import com.academy.exception.MentorSessionNotFoundException;
import com.academy.exception.StudentNotFoundException;
import com.academy.kafka.producer.MentorSessionEventProducer;
import com.academy.mapper.MentorSessionMapper;
import com.academy.repository.MentorRepository;
import com.academy.repository.MentorSessionRepository;
import com.academy.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for MentorSession operations
 * Report: Feature Development Process - Core service for "Book a Mentor Session" feature
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MentorSessionService {
    
    private final MentorSessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final MentorSessionMapper sessionMapper;
    private final MentorSessionEventProducer eventProducer;
    
    @Transactional(readOnly = true)
    @Cacheable(value = "mentorSessions", key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<MentorSessionDTO> getAllSessions() {
        return sessionRepository.findAll().stream()
            .map(sessionMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "mentorSession", key = "'mentorSession:' + #id", unless = "#result == null")
    public MentorSessionDTO getSessionById(Long id) {
        MentorSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new MentorSessionNotFoundException(id));
        return sessionMapper.toDTO(session);
    }
    
    @Transactional
    @CacheEvict(value = {"mentorSession", "mentorSessions"}, allEntries = true)
    public MentorSessionDTO createSession(MentorSessionDTO dto) {
        log.info("Creating mentor session for student: {} with mentor: {}", dto.getStudentId(), dto.getMentorId());
        
        Student student = studentRepository.findById(dto.getStudentId())
            .orElseThrow(() -> new StudentNotFoundException(dto.getStudentId()));
        
        Mentor mentor = mentorRepository.findById(dto.getMentorId())
            .orElseThrow(() -> new MentorNotFoundException(dto.getMentorId()));
        
        MentorSession session = sessionMapper.toEntity(dto);
        session.setStudent(student);
        session.setMentor(mentor);
        
        MentorSession saved = sessionRepository.save(session);
        log.info("Mentor session created with id: {}", saved.getId());
        
        // Report: Feature Development Process - Publish Kafka event after session creation
        eventProducer.publishSessionCreatedEvent(saved);
        
        return sessionMapper.toDTO(saved);
    }
    
    @Transactional
    @CacheEvict(value = {"mentorSession", "mentorSessions"}, key = "'mentorSession:' + #id", allEntries = true)
    public MentorSessionDTO updateSession(Long id, MentorSessionDTO dto) {
        MentorSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new MentorSessionNotFoundException(id));
        
        session.setTime(java.sql.Timestamp.from(dto.getTime()));
        session.setDurationMinutes(dto.getDurationMinutes());
        session.setStudentRating(dto.getStudentRating());
        session.setMentorRating(dto.getMentorRating());
        
        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(dto.getStudentId()));
            session.setStudent(student);
        }
        
        if (dto.getMentorId() != null) {
            Mentor mentor = mentorRepository.findById(dto.getMentorId())
                .orElseThrow(() -> new MentorNotFoundException(dto.getMentorId()));
            session.setMentor(mentor);
        }
        
        MentorSession updated = sessionRepository.save(session);
        return sessionMapper.toDTO(updated);
    }
    
    @Transactional
    @CacheEvict(value = {"mentorSession", "mentorSessions"}, key = "'mentorSession:' + #id", allEntries = true)
    public void deleteSession(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new MentorSessionNotFoundException(id);
        }
        sessionRepository.deleteById(id);
        log.info("Mentor session deleted with id: {}", id);
    }
}
