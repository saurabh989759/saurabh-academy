package com.academy.controller;

import com.academy.dto.StudentDTO;
import com.academy.generated.api.StudentsApi;
import com.academy.generated.model.PageStudent;
import com.academy.generated.model.Student;
import com.academy.generated.model.StudentInput;
import com.academy.mapper.ApiModelMapper;
import com.academy.service.StudentService;
import com.academy.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller implementing generated StudentsApi interface
 * Uses generated request/response models from OpenAPI
 * Directly delegates to StudentService
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentController implements StudentsApi {
    
    private final StudentService studentService;
    private final ApiModelMapper apiModelMapper;
    
    @Override
    public ResponseEntity<Student> createStudent(StudentInput studentInput) {
        log.debug("Creating student: {}", studentInput.getEmail());
        StudentDTO dto = apiModelMapper.toDTO(studentInput);
        StudentDTO created = studentService.createStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiModelMapper.toModel(created));
    }
    
    @Override
    public ResponseEntity<Void> deleteStudent(Long id) {
        log.debug("Deleting student: {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<List<Student>> getAllStudents(Long batchId) {
        log.debug("Getting all students, batchId: {}", batchId);
        List<StudentDTO> dtos = studentService.getAllStudents(batchId);
        return ResponseEntity.ok(apiModelMapper.toStudentModelList(dtos));
    }
    
    @Override
    public ResponseEntity<PageStudent> getAllStudentsPaged(Integer page, Integer size, String sort) {
        log.debug("Getting paginated students, page: {}, size: {}, sort: {}", page, size, sort);
        Pageable pageable = PageableUtil.createPageable(page, size, sort);
        Page<StudentDTO> dtoPage = studentService.getAllStudents(pageable);
        return ResponseEntity.ok(apiModelMapper.toPageStudent(dtoPage));
    }
    
    @Override
    public ResponseEntity<Student> getStudentById(Long id) {
        log.debug("Getting student by id: {}", id);
        StudentDTO dto = studentService.getStudentById(id);
        return ResponseEntity.ok(apiModelMapper.toModel(dto));
    }
    
    @Override
    public ResponseEntity<Student> updateStudent(Long id, StudentInput studentInput) {
        log.debug("Updating student: {}", id);
        StudentDTO dto = apiModelMapper.toDTO(studentInput);
        StudentDTO updated = studentService.updateStudent(id, dto);
        return ResponseEntity.ok(apiModelMapper.toModel(updated));
    }
}

