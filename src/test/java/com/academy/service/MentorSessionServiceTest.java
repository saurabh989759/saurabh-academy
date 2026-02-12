package com.academy.service;

import com.academy.dto.MentorSessionDTO;
import com.academy.entity.Mentor;
import com.academy.entity.MentorSession;
import com.academy.entity.Student;
import com.academy.kafka.MentorSessionEventProducer;
import com.academy.mapper.MentorSessionMapper;
import com.academy.repository.MentorRepository;
import com.academy.repository.MentorSessionRepository;
import com.academy.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MentorSessionService
 * Report: Tests - Unit tests for "Book a Mentor Session" feature
 */
@ExtendWith(MockitoExtension.class)
class MentorSessionServiceTest {
    
    @Mock
    private MentorSessionRepository sessionRepository;
    
    @Mock
    private StudentRepository studentRepository;
    
    @Mock
    private MentorRepository mentorRepository;
    
    @Mock
    private MentorSessionMapper sessionMapper;
    
    @Mock
    private MentorSessionEventProducer eventProducer;
    
    @InjectMocks
    private MentorSessionService sessionService;
    
    private MentorSessionDTO sessionDTO;
    private MentorSession session;
    private Student student;
    private Mentor mentor;
    
    @BeforeEach
    void setUp() {
        sessionDTO = new MentorSessionDTO();
        sessionDTO.setTime(Instant.now());
        sessionDTO.setDurationMinutes(60);
        sessionDTO.setStudentId(1L);
        sessionDTO.setMentorId(1L);
        
        student = new Student();
        student.setId(1L);
        student.setName("Test Student");
        
        mentor = new Mentor();
        mentor.setId(1L);
        mentor.setName("Test Mentor");
        
        session = new MentorSession();
        session.setId(1L);
        session.setStudent(student);
        session.setMentor(mentor);
    }
    
    @Test
    void testCreateSession() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(sessionMapper.toEntity(sessionDTO)).thenReturn(session);
        when(sessionRepository.save(any(MentorSession.class))).thenReturn(session);
        when(sessionMapper.toDTO(session)).thenReturn(sessionDTO);
        
        MentorSessionDTO result = sessionService.createSession(sessionDTO);
        
        assertNotNull(result);
        verify(eventProducer, times(1)).publishSessionCreatedEvent(any(MentorSession.class));
        verify(sessionRepository, times(1)).save(any(MentorSession.class));
    }
}

