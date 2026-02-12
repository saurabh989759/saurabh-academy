package com.academy.controller;

import com.academy.dto.ClassDTO;
import com.academy.generated.api.ClassesApi;
import com.academy.generated.model.ClassInput;
import com.academy.generated.model.ModelClass;
import com.academy.mapper.ApiModelMapper;
import com.academy.service.ClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller implementing generated ClassesApi interface
 * Uses generated request/response models from OpenAPI
 * Directly delegates to ClassService
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClassController implements ClassesApi {
    
    private final ClassService classService;
    private final ApiModelMapper apiModelMapper;
    
    @Override
    public ResponseEntity<ModelClass> createClass(ClassInput classInput) {
        log.debug("Creating class: {}", classInput.getName());
        ClassDTO dto = apiModelMapper.toDTO(classInput);
        ClassDTO created = classService.createClass(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiModelMapper.toModel(created));
    }
    
    @Override
    public ResponseEntity<Void> deleteClass(Long id) {
        log.debug("Deleting class: {}", id);
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<List<ModelClass>> getAllClasses() {
        log.debug("Getting all classes");
        List<ClassDTO> dtos = classService.getAllClasses();
        return ResponseEntity.ok(apiModelMapper.toClassModelList(dtos));
    }
    
    @Override
    public ResponseEntity<ModelClass> getClassById(Long id) {
        log.debug("Getting class by id: {}", id);
        ClassDTO dto = classService.getClassById(id);
        return ResponseEntity.ok(apiModelMapper.toModel(dto));
    }
    
    @Override
    public ResponseEntity<ModelClass> updateClass(Long id, ClassInput classInput) {
        log.debug("Updating class: {}", id);
        ClassDTO dto = apiModelMapper.toDTO(classInput);
        ClassDTO updated = classService.updateClass(id, dto);
        return ResponseEntity.ok(apiModelMapper.toModel(updated));
    }
}

