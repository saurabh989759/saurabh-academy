package com.academy.service;

import com.academy.dto.MentorDTO;
import com.academy.entity.Mentor;
import com.academy.exception.MentorNotFoundException;
import com.academy.mapper.MentorMapper;
import com.academy.repository.MentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.academy.util.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for MentorService
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MentorService Tests")
class MentorServiceTest {
    
    @Mock
    private MentorRepository mentorRepository;
    
    @Mock
    private MentorMapper mentorMapper;
    
    @InjectMocks
    private MentorService mentorService;
    
    private Mentor mentorEntity;
    private MentorDTO mentorDTO;
    
    @BeforeEach
    void setUp() {
        mentorEntity = mentorEntity(1L, "Test Mentor");
        mentorDTO = mentorDTO().id(1L).name("Test Mentor").build();
    }
    
    @Test
    @DisplayName("Should return all mentors")
    void getAllMentors_ReturnsAllMentors() {
        // Given
        List<Mentor> mentors = Arrays.asList(mentorEntity);
        when(mentorRepository.findAll()).thenReturn(mentors);
        when(mentorMapper.toDTO(any(Mentor.class))).thenReturn(mentorDTO);
        
        // When
        List<MentorDTO> result = mentorService.getAllMentors();
        
        // Then
        assertThat(result).hasSize(1);
        verify(mentorRepository).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no mentors")
    void getAllMentors_WhenEmpty_ReturnsEmptyList() {
        // Given
        when(mentorRepository.findAll()).thenReturn(List.of());
        
        // When
        List<MentorDTO> result = mentorService.getAllMentors();
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should return mentor by id when found")
    void getMentorById_WhenExists_ReturnsMentor() {
        // Given
        Long id = 1L;
        when(mentorRepository.findById(id)).thenReturn(Optional.of(mentorEntity));
        when(mentorMapper.toDTO(mentorEntity)).thenReturn(mentorDTO);
        
        // When
        MentorDTO result = mentorService.getMentorById(id);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Should throw MentorNotFoundException when not found")
    void getMentorById_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(mentorRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> mentorService.getMentorById(id))
            .isInstanceOf(MentorNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should create mentor successfully")
    void createMentor_WhenValid_ReturnsCreatedMentor() {
        // Given
        MentorDTO inputDTO = mentorDTO().name("New Mentor").build();
        Mentor newMentor = mentorEntity(null, "New Mentor");
        Mentor savedMentor = mentorEntity(2L, "New Mentor");
        MentorDTO savedDTO = mentorDTO().id(2L).name("New Mentor").build();
        
        when(mentorMapper.toEntity(inputDTO)).thenReturn(newMentor);
        when(mentorRepository.save(any(Mentor.class))).thenReturn(savedMentor);
        when(mentorMapper.toDTO(savedMentor)).thenReturn(savedDTO);
        
        // When
        MentorDTO result = mentorService.createMentor(inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        verify(mentorRepository).save(any(Mentor.class));
    }
    
    @Test
    @DisplayName("Should update mentor successfully")
    void updateMentor_WhenValid_ReturnsUpdatedMentor() {
        // Given
        Long id = 1L;
        MentorDTO inputDTO = mentorDTO().id(id).name("Updated Mentor").build();
        MentorDTO updatedDTO = mentorDTO().id(id).name("Updated Mentor").build();
        
        when(mentorRepository.findById(id)).thenReturn(Optional.of(mentorEntity));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentorEntity);
        when(mentorMapper.toDTO(any(Mentor.class))).thenReturn(updatedDTO);
        
        // When
        MentorDTO result = mentorService.updateMentor(id, inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(mentorRepository).findById(id);
        verify(mentorRepository).save(any(Mentor.class));
    }
    
    @Test
    @DisplayName("Should throw MentorNotFoundException when updating non-existent mentor")
    void updateMentor_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        MentorDTO inputDTO = mentorDTO().build();
        when(mentorRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> mentorService.updateMentor(id, inputDTO))
            .isInstanceOf(MentorNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should delete mentor successfully")
    void deleteMentor_WhenExists_DeletesMentor() {
        // Given
        Long id = 1L;
        when(mentorRepository.existsById(id)).thenReturn(true);
        
        // When
        mentorService.deleteMentor(id);
        
        // Then
        verify(mentorRepository).existsById(id);
        verify(mentorRepository).deleteById(id);
    }
    
    @Test
    @DisplayName("Should throw MentorNotFoundException when deleting non-existent mentor")
    void deleteMentor_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(mentorRepository.existsById(id)).thenReturn(false);
        
        // When/Then
        assertThatThrownBy(() -> mentorService.deleteMentor(id))
            .isInstanceOf(MentorNotFoundException.class);
        verify(mentorRepository, never()).deleteById(any());
    }
}

