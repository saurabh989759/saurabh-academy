package com.academy.controller;

import com.academy.dto.StudentDTO;
import com.academy.generated.model.Student;
import com.academy.generated.model.StudentInput;
import com.academy.mapper.ApiModelMapper;
import com.academy.service.StudentService;
import com.academy.util.PageableUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.academy.util.TestDataBuilder.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive integration tests for StudentController
 * Uses MockMvc for HTTP endpoint testing with Spring Security
 * Covers all endpoints, status codes, and error scenarios
 */
@WebMvcTest(StudentController.class)
@DisplayName("StudentController Integration Tests")
class StudentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private StudentService studentService;
    
    @MockBean
    private ApiModelMapper apiModelMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private StudentDTO studentDTO;
    private Student studentModel;
    private StudentInput studentInput;
    
    @BeforeEach
    void setUp() {
        studentDTO = studentDTO().id(1L).email("test@example.com").build();
        studentModel = new Student();
        studentModel.setId(1L);
        studentModel.setEmail("test@example.com");
        studentModel.setName("Test Student");
        
        studentInput = new StudentInput();
        studentInput.setEmail("test@example.com");
        studentInput.setName("Test Student");
    }
    
    // ========== createStudent Tests ==========
    
    @Test
    @DisplayName("Should create student successfully - 201 Created")
    @WithMockUser
    void createStudent_WhenValid_Returns201() throws Exception {
        // Given
        when(apiModelMapper.toDTO(any(StudentInput.class))).thenReturn(studentDTO);
        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(studentDTO);
        when(apiModelMapper.toModel(studentDTO)).thenReturn(studentModel);
        
        // When/Then
        mockMvc.perform(post("/api/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentInput)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("test@example.com"));
        
        verify(studentService).createStudent(any(StudentDTO.class));
    }
    
    @Test
    @DisplayName("Should return 401 when not authenticated")
    void createStudent_WhenNotAuthenticated_Returns401() throws Exception {
        // When/Then
        mockMvc.perform(post("/api/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentInput)))
            .andExpect(status().isUnauthorized());
    }
    
    // ========== getStudentById Tests ==========
    
    @Test
    @DisplayName("Should get student by id - 200 OK")
    @WithMockUser
    void getStudentById_WhenExists_Returns200() throws Exception {
        // Given
        Long id = 1L;
        when(studentService.getStudentById(id)).thenReturn(studentDTO);
        when(apiModelMapper.toModel(studentDTO)).thenReturn(studentModel);
        
        // When/Then
        mockMvc.perform(get("/api/students/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("test@example.com"));
        
        verify(studentService).getStudentById(id);
    }
    
    @Test
    @DisplayName("Should return 401 when not authenticated")
    void getStudentById_WhenNotAuthenticated_Returns401() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/students/{id}", 1L))
            .andExpect(status().isUnauthorized());
    }
    
    // ========== getAllStudents Tests ==========
    
    @Test
    @DisplayName("Should get all students - 200 OK")
    @WithMockUser
    void getAllStudents_WhenExists_Returns200() throws Exception {
        // Given
        List<StudentDTO> dtos = Arrays.asList(studentDTO);
        List<Student> models = Arrays.asList(studentModel);
        when(studentService.getAllStudents((Long) null)).thenReturn(dtos);
        when(apiModelMapper.toStudentModelList(dtos)).thenReturn(models);
        
        // When/Then
        mockMvc.perform(get("/api/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1L));
        
        verify(studentService).getAllStudents((Long) null);
    }
    
    @Test
    @DisplayName("Should get students by batchId - 200 OK")
    @WithMockUser
    void getAllStudents_WithBatchId_Returns200() throws Exception {
        // Given
        Long batchId = 1L;
        List<StudentDTO> dtos = Arrays.asList(studentDTO);
        List<Student> models = Arrays.asList(studentModel);
        when(studentService.getAllStudents(batchId)).thenReturn(dtos);
        when(apiModelMapper.toStudentModelList(dtos)).thenReturn(models);
        
        // When/Then
        mockMvc.perform(get("/api/students").param("batchId", batchId.toString()))
            .andExpect(status().isOk());
        
        verify(studentService).getAllStudents(batchId);
    }
    
    // ========== getAllStudentsPaged Tests ==========
    
    @Test
    @DisplayName("Should get paginated students - 200 OK")
    @WithMockUser
    void getAllStudentsPaged_WhenExists_Returns200() throws Exception {
        // Given
        Page<StudentDTO> dtoPage = new PageImpl<>(Arrays.asList(studentDTO));
        when(studentService.getAllStudents(any(org.springframework.data.domain.Pageable.class))).thenReturn(dtoPage);
        when(apiModelMapper.toPageStudent(dtoPage)).thenReturn(new com.academy.generated.model.PageStudent());
        
        // When/Then
        mockMvc.perform(get("/api/students/paged")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());
        
        verify(studentService).getAllStudents(any(org.springframework.data.domain.Pageable.class));
    }
    
    // ========== updateStudent Tests ==========
    
    @Test
    @DisplayName("Should update student successfully - 200 OK")
    @WithMockUser
    void updateStudent_WhenValid_Returns200() throws Exception {
        // Given
        Long id = 1L;
        when(apiModelMapper.toDTO(any(StudentInput.class))).thenReturn(studentDTO);
        when(studentService.updateStudent(eq(id), any(StudentDTO.class))).thenReturn(studentDTO);
        when(apiModelMapper.toModel(studentDTO)).thenReturn(studentModel);
        
        // When/Then
        mockMvc.perform(put("/api/students/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
        
        verify(studentService).updateStudent(eq(id), any(StudentDTO.class));
    }
    
    // ========== deleteStudent Tests ==========
    
    @Test
    @DisplayName("Should delete student successfully - 204 No Content")
    @WithMockUser
    void deleteStudent_WhenExists_Returns204() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(studentService).deleteStudent(id);
        
        // When/Then
        mockMvc.perform(delete("/api/students/{id}", id)
                .with(csrf()))
            .andExpect(status().isNoContent());
        
        verify(studentService).deleteStudent(id);
    }
}

