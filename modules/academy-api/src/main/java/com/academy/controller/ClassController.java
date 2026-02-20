package com.academy.controller;

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

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClassController implements ClassesApi {

    private final ClassService classService;
    private final ApiModelMapper mapper;

    @Override
    public ResponseEntity<ModelClass> createClass(ClassInput classInput) {
        log.debug("POST /classes — name={}", classInput.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toModel(classService.createClass(mapper.toDTO(classInput))));
    }

    @Override
    public ResponseEntity<ModelClass> updateClass(Long id, ClassInput classInput) {
        log.debug("PUT /classes/{}", id);
        return ResponseEntity.ok(mapper.toModel(classService.updateClass(id, mapper.toDTO(classInput))));
    }

    @Override
    public ResponseEntity<Void> deleteClass(Long id) {
        log.debug("DELETE /classes/{}", id);
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ModelClass> getClassById(Long id) {
        log.debug("GET /classes/{}", id);
        return ResponseEntity.ok(mapper.toModel(classService.getClassById(id)));
    }

    @Override
    public ResponseEntity<List<ModelClass>> getAllClasses() {
        log.debug("GET /classes");
        return ResponseEntity.ok(mapper.toClassModelList(classService.getAllClasses()));
    }
}
