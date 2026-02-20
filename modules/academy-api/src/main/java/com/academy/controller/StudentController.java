package com.academy.controller;

import com.academy.dto.StudentDTO;
import com.academy.generated.api.StudentsApi;
import com.academy.generated.model.PageStudent;
import com.academy.generated.model.Student;
import com.academy.generated.model.StudentInput;
import com.academy.mapper.ApiModelMapper;
import com.academy.service.StudentService;
import com.academy.service.WebSocketEventPublisher;
import com.academy.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentController implements StudentsApi {

    private final StudentService studentService;
    private final ApiModelMapper mapper;
    private final WebSocketEventPublisher eventPublisher;

    @Override
    public ResponseEntity<Student> createStudent(StudentInput studentInput) {
        log.debug("POST /students — email={}", studentInput.getEmail());
        StudentDTO created = studentService.createStudent(mapper.toDTO(studentInput));
        eventPublisher.publishStudentCreated(created.getId(), created.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toModel(created));
    }

    @Override
    public ResponseEntity<Student> updateStudent(Long id, StudentInput studentInput) {
        log.debug("PUT /students/{}", id);
        StudentDTO updated = studentService.updateStudent(id, mapper.toDTO(studentInput));
        eventPublisher.publishStudentUpdated(updated.getId(), updated.getEmail());
        return ResponseEntity.ok(mapper.toModel(updated));
    }

    @Override
    public ResponseEntity<Void> deleteStudent(Long id) {
        log.debug("DELETE /students/{}", id);
        studentService.deleteStudent(id);
        eventPublisher.publishStudentDeleted(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Student> getStudentById(Long id) {
        log.debug("GET /students/{}", id);
        return ResponseEntity.ok(mapper.toModel(studentService.getStudentById(id)));
    }

    @Override
    public ResponseEntity<List<Student>> getAllStudents(Long batchId) {
        log.debug("GET /students batchId={}", batchId);
        return ResponseEntity.ok(mapper.toStudentModelList(studentService.getAllStudents(batchId)));
    }

    @Override
    public ResponseEntity<PageStudent> getAllStudentsPaged(Integer page, Integer size, String sort) {
        log.debug("GET /students/paged page={} size={}", page, size);
        Pageable pageable = PageableUtil.createPageable(page, size, sort);
        Page<StudentDTO> resultPage = studentService.getAllStudents(pageable);
        return ResponseEntity.ok(mapper.toPageStudent(resultPage));
    }
}
