package com.academy.service;

import com.academy.dto.StudentDTO;
import com.academy.entity.Batch;
import com.academy.entity.Student;
import com.academy.exception.BatchNotFoundException;
import com.academy.exception.StudentNotFoundException;
import com.academy.kafka.producer.StudentEventProducer;
import com.academy.mapper.StudentMapper;
import com.academy.repository.BatchRepository;
import com.academy.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.academy.util.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for StudentService
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Tests")
class StudentServiceTest {
    
    @Mock
    private StudentRepository studentRepository;
    
    @Mock
    private BatchRepository batchRepository;
    
    @Mock
    private StudentMapper studentMapper;
    
    @Mock
    private StudentEventProducer eventProducer;
    
    @InjectMocks
    private StudentService studentService;
    
    private Student studentEntity;
    private StudentDTO studentDTO;
    private Batch batchEntity;
    
    @BeforeEach
    void setUp() {
        studentEntity = studentEntity(1L, "test@example.com");
        studentDTO = studentDTO().id(1L).email("test@example.com").build();
        batchEntity = batchEntity(1L, "Test Batch");
    }
    
    // ========== getAllStudents(Long batchId) Tests ==========
    
    @Test
    @DisplayName("Should return all students when batchId is null")
    void getAllStudents_WhenBatchIdIsNull_ReturnsAllStudents() {
        // Given
        List<Student> students = Arrays.asList(studentEntity);
        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.toDTO(any(Student.class))).thenReturn(studentDTO);
        
        // When
        List<StudentDTO> result = studentService.getAllStudents((Long) null);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
        verify(studentRepository).findAll();
        verify(studentRepository, never()).findByBatchId(any());
    }
    
    @Test
    @DisplayName("Should return students by batchId when batchId is provided")
    void getAllStudents_WhenBatchIdProvided_ReturnsStudentsByBatch() {
        // Given
        Long batchId = 1L;
        List<Student> students = Arrays.asList(studentEntity);
        when(studentRepository.findByBatchId(batchId)).thenReturn(students);
        when(studentMapper.toDTO(any(Student.class))).thenReturn(studentDTO);
        
        // When
        List<StudentDTO> result = studentService.getAllStudents(batchId);
        
        // Then
        assertThat(result).hasSize(1);
        verify(studentRepository).findByBatchId(batchId);
        verify(studentRepository, never()).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no students found")
    void getAllStudents_WhenNoStudents_ReturnsEmptyList() {
        // Given
        when(studentRepository.findAll()).thenReturn(List.of());
        
        // When
        List<StudentDTO> result = studentService.getAllStudents((Long) null);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    // ========== getAllStudents(Pageable) Tests ==========
    
    @Test
    @DisplayName("Should return paginated students")
    void getAllStudents_WithPageable_ReturnsPaginatedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> studentPage = new PageImpl<>(Arrays.asList(studentEntity));
        when(studentRepository.findAll(pageable)).thenReturn(studentPage);
        when(studentMapper.toDTO(any(Student.class))).thenReturn(studentDTO);
        
        // When
        Page<StudentDTO> result = studentService.getAllStudents(pageable);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(studentRepository).findAll(pageable);
    }
    
    // ========== getStudentById Tests ==========
    
    @Test
    @DisplayName("Should return student when found")
    void getStudentById_WhenExists_ReturnsStudent() {
        // Given
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.of(studentEntity));
        when(studentMapper.toDTO(studentEntity)).thenReturn(studentDTO);
        
        // When
        StudentDTO result = studentService.getStudentById(id);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(studentRepository).findById(id);
    }
    
    @Test
    @DisplayName("Should throw StudentNotFoundException when not found")
    void getStudentById_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(studentRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> studentService.getStudentById(id))
            .isInstanceOf(StudentNotFoundException.class)
            .hasMessageContaining("999");
        verify(studentRepository).findById(id);
    }
    
    // ========== createStudent Tests ==========
    
    @Test
    @DisplayName("Should create student successfully")
    void createStudent_WhenValid_ReturnsCreatedStudent() {
        // Given
        StudentDTO inputDTO = studentDTO().email("new@example.com").batchId(1L).build();
        Student newStudent = studentEntity(null, "new@example.com");
        Student savedStudent = studentEntity(2L, "new@example.com");
        StudentDTO savedDTO = studentDTO().id(2L).email("new@example.com").build();
        
        when(studentRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batchEntity));
        when(studentMapper.toEntity(inputDTO)).thenReturn(newStudent);
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(studentMapper.toDTO(savedStudent)).thenReturn(savedDTO);
        
        // When
        StudentDTO result = studentService.createStudent(inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        verify(studentRepository).findByEmail("new@example.com");
        verify(batchRepository).findById(1L);
        verify(studentRepository).save(any(Student.class));
        verify(eventProducer).publishStudentRegisteredEvent(savedStudent);
    }
    
    @Test
    @DisplayName("Should create student without batch when batchId is null")
    void createStudent_WhenNoBatchId_CreatesStudentWithoutBatch() {
        // Given
        StudentDTO inputDTO = studentDTO().email("new@example.com").batchId(null).build();
        Student newStudent = studentEntity(null, "new@example.com");
        Student savedStudent = studentEntity(2L, "new@example.com");
        StudentDTO savedDTO = studentDTO().id(2L).email("new@example.com").build();
        
        when(studentRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(studentMapper.toEntity(inputDTO)).thenReturn(newStudent);
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(studentMapper.toDTO(savedStudent)).thenReturn(savedDTO);
        
        // When
        StudentDTO result = studentService.createStudent(inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(batchRepository, never()).findById(any());
    }
    
    @Test
    @DisplayName("Should throw exception when email already exists")
    void createStudent_WhenEmailExists_ThrowsException() {
        // Given
        StudentDTO inputDTO = studentDTO().email("existing@example.com").build();
        when(studentRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(studentEntity));
        
        // When/Then
        assertThatThrownBy(() -> studentService.createStudent(inputDTO))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already exists");
        verify(studentRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw BatchNotFoundException when batch not found")
    void createStudent_WhenBatchNotFound_ThrowsException() {
        // Given
        StudentDTO inputDTO = studentDTO().email("new@example.com").batchId(999L).build();
        when(studentRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(batchRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> studentService.createStudent(inputDTO))
            .isInstanceOf(BatchNotFoundException.class);
        verify(studentRepository, never()).save(any());
    }
    
    // ========== updateStudent Tests ==========
    
    @Test
    @DisplayName("Should update student successfully")
    void updateStudent_WhenValid_ReturnsUpdatedStudent() {
        // Given
        Long id = 1L;
        StudentDTO inputDTO = studentDTO().id(id).email("updated@example.com").name("Updated Name").build();
        StudentDTO updatedDTO = studentDTO().id(id).email("updated@example.com").name("Updated Name").build();
        
        when(studentRepository.findByIdWithLock(id)).thenReturn(Optional.of(studentEntity));
        when(studentRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(studentEntity);
        when(studentMapper.toDTO(any(Student.class))).thenReturn(updatedDTO);
        
        // When
        StudentDTO result = studentService.updateStudent(id, inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(studentRepository).findByIdWithLock(id);
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @DisplayName("Should throw StudentNotFoundException when student not found")
    void updateStudent_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        StudentDTO inputDTO = studentDTO().build();
        when(studentRepository.findByIdWithLock(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> studentService.updateStudent(id, inputDTO))
            .isInstanceOf(StudentNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should allow email update when email is same")
    void updateStudent_WhenEmailSame_AllowsUpdate() {
        // Given
        Long id = 1L;
        StudentDTO inputDTO = studentDTO().id(id).email("test@example.com").build();
        
        when(studentRepository.findByIdWithLock(id)).thenReturn(Optional.of(studentEntity));
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(studentEntity));
        when(studentRepository.save(any(Student.class))).thenReturn(studentEntity);
        when(studentMapper.toDTO(any(Student.class))).thenReturn(studentDTO);
        
        // When
        StudentDTO result = studentService.updateStudent(id, inputDTO);
        
        // Then
        assertThat(result).isNotNull();
    }
    
    @Test
    @DisplayName("Should throw exception when new email already exists for different student")
    void updateStudent_WhenEmailExistsForOther_ThrowsException() {
        // Given
        Long id = 1L;
        StudentDTO inputDTO = studentDTO().id(id).email("other@example.com").build();
        Student otherStudent = studentEntity(2L, "other@example.com");
        
        when(studentRepository.findByIdWithLock(id)).thenReturn(Optional.of(studentEntity));
        when(studentRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherStudent));
        
        // When/Then
        assertThatThrownBy(() -> studentService.updateStudent(id, inputDTO))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already exists");
    }
    
    @Test
    @DisplayName("Should update batch when batchId provided")
    void updateStudent_WhenBatchIdProvided_UpdatesBatch() {
        // Given
        Long id = 1L;
        Long newBatchId = 2L;
        StudentDTO inputDTO = studentDTO().id(id).batchId(newBatchId).build();
        Batch newBatch = batchEntity(2L, "New Batch");
        
        when(studentRepository.findByIdWithLock(id)).thenReturn(Optional.of(studentEntity));
        when(studentRepository.findByEmail(any())).thenReturn(Optional.of(studentEntity));
        when(batchRepository.findByIdWithLock(newBatchId)).thenReturn(Optional.of(newBatch));
        when(studentRepository.save(any(Student.class))).thenReturn(studentEntity);
        when(studentMapper.toDTO(any(Student.class))).thenReturn(studentDTO);
        
        // When
        studentService.updateStudent(id, inputDTO);
        
        // Then
        verify(batchRepository).findByIdWithLock(newBatchId);
    }
    
    // ========== deleteStudent Tests ==========
    
    @Test
    @DisplayName("Should delete student successfully")
    void deleteStudent_WhenExists_DeletesStudent() {
        // Given
        Long id = 1L;
        when(studentRepository.existsById(id)).thenReturn(true);
        
        // When
        studentService.deleteStudent(id);
        
        // Then
        verify(studentRepository).existsById(id);
        verify(studentRepository).deleteById(id);
    }
    
    @Test
    @DisplayName("Should throw StudentNotFoundException when student not found for deletion")
    void deleteStudent_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(studentRepository.existsById(id)).thenReturn(false);
        
        // When/Then
        assertThatThrownBy(() -> studentService.deleteStudent(id))
            .isInstanceOf(StudentNotFoundException.class);
        verify(studentRepository, never()).deleteById(any());
    }
}

