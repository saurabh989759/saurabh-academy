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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.academy.util.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for MentorSessionService
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MentorSessionService Tests")
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
    
    private MentorSession sessionEntity;
    private MentorSessionDTO sessionDTO;
    private Student studentEntity;
    private Mentor mentorEntity;
    
    @BeforeEach
    void setUp() {
        studentEntity = studentEntity(1L, "student@example.com");
        mentorEntity = mentorEntity(1L, "Test Mentor");
        sessionEntity = mentorSessionEntity(1L, studentEntity, mentorEntity);
        sessionDTO = mentorSessionDTO().id(1L).studentId(1L).mentorId(1L).build();
    }
    
    @Test
    @DisplayName("Should return all sessions")
    void getAllSessions_ReturnsAllSessions() {
        // Given
        List<MentorSession> sessions = Arrays.asList(sessionEntity);
        when(sessionRepository.findAll()).thenReturn(sessions);
        when(sessionMapper.toDTO(any(MentorSession.class))).thenReturn(sessionDTO);
        
        // When
        List<MentorSessionDTO> result = sessionService.getAllSessions();
        
        // Then
        assertThat(result).hasSize(1);
        verify(sessionRepository).findAll();
    }
    
    @Test
    @DisplayName("Should return session by id when found")
    void getSessionById_WhenExists_ReturnsSession() {
        // Given
        Long id = 1L;
        when(sessionRepository.findById(id)).thenReturn(Optional.of(sessionEntity));
        when(sessionMapper.toDTO(sessionEntity)).thenReturn(sessionDTO);
        
        // When
        MentorSessionDTO result = sessionService.getSessionById(id);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Should throw MentorSessionNotFoundException when not found")
    void getSessionById_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(sessionRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> sessionService.getSessionById(id))
            .isInstanceOf(MentorSessionNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should create session successfully")
    void createSession_WhenValid_ReturnsCreatedSession() {
        // Given
        MentorSessionDTO inputDTO = mentorSessionDTO().studentId(1L).mentorId(1L).build();
        MentorSession newSession = mentorSessionEntity(null, studentEntity, mentorEntity);
        MentorSession savedSession = mentorSessionEntity(2L, studentEntity, mentorEntity);
        MentorSessionDTO savedDTO = mentorSessionDTO().id(2L).build();
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentorEntity));
        when(sessionMapper.toEntity(inputDTO)).thenReturn(newSession);
        when(sessionRepository.save(any(MentorSession.class))).thenReturn(savedSession);
        when(sessionMapper.toDTO(savedSession)).thenReturn(savedDTO);
        
        // When
        MentorSessionDTO result = sessionService.createSession(inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(studentRepository).findById(1L);
        verify(mentorRepository).findById(1L);
        verify(sessionRepository).save(any(MentorSession.class));
        verify(eventProducer).publishSessionCreatedEvent(savedSession);
    }
    
    @Test
    @DisplayName("Should throw StudentNotFoundException when student not found")
    void createSession_WhenStudentNotFound_ThrowsException() {
        // Given
        MentorSessionDTO inputDTO = mentorSessionDTO().studentId(999L).mentorId(1L).build();
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> sessionService.createSession(inputDTO))
            .isInstanceOf(StudentNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should throw MentorNotFoundException when mentor not found")
    void createSession_WhenMentorNotFound_ThrowsException() {
        // Given
        MentorSessionDTO inputDTO = mentorSessionDTO().studentId(1L).mentorId(999L).build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(mentorRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> sessionService.createSession(inputDTO))
            .isInstanceOf(MentorNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should update session successfully")
    void updateSession_WhenValid_ReturnsUpdatedSession() {
        // Given
        Long id = 1L;
        MentorSessionDTO inputDTO = mentorSessionDTO().id(id).durationMinutes(90).build();
        MentorSessionDTO updatedDTO = mentorSessionDTO().id(id).durationMinutes(90).build();
        
        when(sessionRepository.findById(id)).thenReturn(Optional.of(sessionEntity));
        when(sessionRepository.save(any(MentorSession.class))).thenReturn(sessionEntity);
        when(sessionMapper.toDTO(any(MentorSession.class))).thenReturn(updatedDTO);
        
        // When
        MentorSessionDTO result = sessionService.updateSession(id, inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(sessionRepository).findById(id);
        verify(sessionRepository).save(any(MentorSession.class));
    }
    
    @Test
    @DisplayName("Should update session with new student when studentId provided")
    void updateSession_WhenStudentIdProvided_UpdatesStudent() {
        // Given
        Long id = 1L;
        Long newStudentId = 2L;
        Student newStudent = studentEntity(2L, "new@example.com");
        MentorSessionDTO inputDTO = mentorSessionDTO().id(id).studentId(newStudentId).build();
        
        when(sessionRepository.findById(id)).thenReturn(Optional.of(sessionEntity));
        when(studentRepository.findById(newStudentId)).thenReturn(Optional.of(newStudent));
        when(sessionRepository.save(any(MentorSession.class))).thenReturn(sessionEntity);
        when(sessionMapper.toDTO(any(MentorSession.class))).thenReturn(sessionDTO);
        
        // When
        sessionService.updateSession(id, inputDTO);
        
        // Then
        verify(studentRepository).findById(newStudentId);
    }
    
    @Test
    @DisplayName("Should update session with new mentor when mentorId provided")
    void updateSession_WhenMentorIdProvided_UpdatesMentor() {
        // Given
        Long id = 1L;
        Long newMentorId = 2L;
        Mentor newMentor = mentorEntity(2L, "New Mentor");
        MentorSessionDTO inputDTO = mentorSessionDTO().id(id).mentorId(newMentorId).build();
        
        when(sessionRepository.findById(id)).thenReturn(Optional.of(sessionEntity));
        when(mentorRepository.findById(newMentorId)).thenReturn(Optional.of(newMentor));
        when(sessionRepository.save(any(MentorSession.class))).thenReturn(sessionEntity);
        when(sessionMapper.toDTO(any(MentorSession.class))).thenReturn(sessionDTO);
        
        // When
        sessionService.updateSession(id, inputDTO);
        
        // Then
        verify(mentorRepository).findById(newMentorId);
    }
    
    @Test
    @DisplayName("Should throw MentorSessionNotFoundException when updating non-existent session")
    void updateSession_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        MentorSessionDTO inputDTO = mentorSessionDTO().build();
        when(sessionRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> sessionService.updateSession(id, inputDTO))
            .isInstanceOf(MentorSessionNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should delete session successfully")
    void deleteSession_WhenExists_DeletesSession() {
        // Given
        Long id = 1L;
        when(sessionRepository.existsById(id)).thenReturn(true);
        
        // When
        sessionService.deleteSession(id);
        
        // Then
        verify(sessionRepository).existsById(id);
        verify(sessionRepository).deleteById(id);
    }
    
    @Test
    @DisplayName("Should throw MentorSessionNotFoundException when deleting non-existent session")
    void deleteSession_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(sessionRepository.existsById(id)).thenReturn(false);
        
        // When/Then
        assertThatThrownBy(() -> sessionService.deleteSession(id))
            .isInstanceOf(MentorSessionNotFoundException.class);
        verify(sessionRepository, never()).deleteById(any());
    }
}

