package com.academy.service;

import com.academy.dto.StudentDTO;
import com.academy.entity.Batch;
import com.academy.entity.Student;
import com.academy.kafka.StudentEventProducer;
import com.academy.mapper.StudentMapper;
import com.academy.repository.BatchRepository;
import com.academy.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService
 */
@ExtendWith(MockitoExtension.class)
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
    
    private StudentDTO studentDTO;
    private Student student;
    private Batch batch;
    
    @BeforeEach
    void setUp() {
        studentDTO = new StudentDTO();
        studentDTO.setName("Test Student");
        studentDTO.setEmail("test@example.com");
        studentDTO.setBatchId(1L);
        
        student = new Student();
        student.setId(1L);
        student.setName("Test Student");
        student.setEmail("test@example.com");
        
        batch = new Batch();
        batch.setId(1L);
        batch.setName("Test Batch");
    }
    
    @Test
    void testCreateStudent() {
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(studentMapper.toEntity(studentDTO)).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toDTO(student)).thenReturn(studentDTO);
        
        StudentDTO result = studentService.createStudent(studentDTO);
        
        assertNotNull(result);
        verify(eventProducer, times(1)).publishStudentRegisteredEvent(any(Student.class));
        verify(studentRepository, times(1)).save(any(Student.class));
    }
    
    @Test
    void testGetStudentById() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toDTO(student)).thenReturn(studentDTO);
        
        StudentDTO result = studentService.getStudentById(1L);
        
        assertNotNull(result);
        assertEquals("Test Student", result.getName());
    }
    
    @Test
    void testGetStudentByIdNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> studentService.getStudentById(1L));
    }
}

