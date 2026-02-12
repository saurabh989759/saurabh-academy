package com.academy.service;

import com.academy.dto.ClassDTO;
import com.academy.entity.ClassEntity;
import com.academy.exception.ClassNotFoundException;
import com.academy.mapper.ClassMapper;
import com.academy.repository.ClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.academy.util.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ClassService
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClassService Tests")
class ClassServiceTest {
    
    @Mock
    private ClassRepository classRepository;
    
    @Mock
    private ClassMapper classMapper;
    
    @InjectMocks
    private ClassService classService;
    
    private ClassEntity classEntity;
    private ClassDTO classDTO;
    
    @BeforeEach
    void setUp() {
        classEntity = classEntity(1L, "Test Class");
        classDTO = classDTO().id(1L).name("Test Class").build();
    }
    
    @Test
    @DisplayName("Should return all classes")
    void getAllClasses_ReturnsAllClasses() {
        // Given
        List<ClassEntity> classes = Arrays.asList(classEntity);
        when(classRepository.findAll()).thenReturn(classes);
        when(classMapper.toDTO(any(ClassEntity.class))).thenReturn(classDTO);
        
        // When
        List<ClassDTO> result = classService.getAllClasses();
        
        // Then
        assertThat(result).hasSize(1);
        verify(classRepository).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no classes")
    void getAllClasses_WhenEmpty_ReturnsEmptyList() {
        // Given
        when(classRepository.findAll()).thenReturn(List.of());
        
        // When
        List<ClassDTO> result = classService.getAllClasses();
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should return class by id when found")
    void getClassById_WhenExists_ReturnsClass() {
        // Given
        Long id = 1L;
        when(classRepository.findById(id)).thenReturn(Optional.of(classEntity));
        when(classMapper.toDTO(classEntity)).thenReturn(classDTO);
        
        // When
        ClassDTO result = classService.getClassById(id);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Should throw ClassNotFoundException when not found")
    void getClassById_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(classRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> classService.getClassById(id))
            .isInstanceOf(ClassNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should create class successfully")
    void createClass_WhenValid_ReturnsCreatedClass() {
        // Given
        ClassDTO inputDTO = classDTO().name("New Class").build();
        ClassEntity newClass = classEntity(null, "New Class");
        ClassEntity savedClass = classEntity(2L, "New Class");
        ClassDTO savedDTO = classDTO().id(2L).name("New Class").build();
        
        when(classMapper.toEntity(inputDTO)).thenReturn(newClass);
        when(classRepository.save(any(ClassEntity.class))).thenReturn(savedClass);
        when(classMapper.toDTO(savedClass)).thenReturn(savedDTO);
        
        // When
        ClassDTO result = classService.createClass(inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        verify(classRepository).save(any(ClassEntity.class));
    }
    
    @Test
    @DisplayName("Should update class successfully")
    void updateClass_WhenValid_ReturnsUpdatedClass() {
        // Given
        Long id = 1L;
        ClassDTO inputDTO = classDTO().id(id).name("Updated Class").build();
        ClassDTO updatedDTO = classDTO().id(id).name("Updated Class").build();
        
        when(classRepository.findById(id)).thenReturn(Optional.of(classEntity));
        when(classRepository.save(any(ClassEntity.class))).thenReturn(classEntity);
        when(classMapper.toDTO(any(ClassEntity.class))).thenReturn(updatedDTO);
        
        // When
        ClassDTO result = classService.updateClass(id, inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(classRepository).findById(id);
        verify(classRepository).save(any(ClassEntity.class));
    }
    
    @Test
    @DisplayName("Should throw ClassNotFoundException when updating non-existent class")
    void updateClass_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        ClassDTO inputDTO = classDTO().build();
        when(classRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> classService.updateClass(id, inputDTO))
            .isInstanceOf(ClassNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should delete class successfully")
    void deleteClass_WhenExists_DeletesClass() {
        // Given
        Long id = 1L;
        when(classRepository.existsById(id)).thenReturn(true);
        
        // When
        classService.deleteClass(id);
        
        // Then
        verify(classRepository).existsById(id);
        verify(classRepository).deleteById(id);
    }
    
    @Test
    @DisplayName("Should throw ClassNotFoundException when deleting non-existent class")
    void deleteClass_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(classRepository.existsById(id)).thenReturn(false);
        
        // When/Then
        assertThatThrownBy(() -> classService.deleteClass(id))
            .isInstanceOf(ClassNotFoundException.class);
        verify(classRepository, never()).deleteById(any());
    }
}

