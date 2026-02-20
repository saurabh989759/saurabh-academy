package com.academy.controller;

import com.academy.dto.BatchTypeDTO;
import com.academy.generated.api.BatchTypesApi;
import com.academy.generated.model.BatchType;
import com.academy.generated.model.BatchTypeInput;
import com.academy.mapper.ApiModelMapper;
import com.academy.service.BatchTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BatchTypeController implements BatchTypesApi {

    private final BatchTypeService batchTypeService;
    private final ApiModelMapper mapper;

    @Override
    public ResponseEntity<List<BatchType>> getAllBatchTypes() {
        log.debug("GET /batch-types");
        List<BatchTypeDTO> all = batchTypeService.getAllBatchTypes();
        return ResponseEntity.ok(mapper.toBatchTypeModelList(all));
    }

    @Override
    public ResponseEntity<BatchType> getBatchTypeById(Long id) {
        log.debug("GET /batch-types/{}", id);
        return ResponseEntity.ok(mapper.toModel(batchTypeService.getBatchTypeById(id)));
    }

    @Override
    public ResponseEntity<BatchType> createBatchType(BatchTypeInput input) {
        log.debug("POST /batch-types — name={}", input.getName());
        BatchTypeDTO created = batchTypeService.createBatchType(mapper.toDTO(input));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toModel(created));
    }

    @Override
    public ResponseEntity<BatchType> updateBatchType(Long id, BatchTypeInput input) {
        log.debug("PUT /batch-types/{}", id);
        BatchTypeDTO updated = batchTypeService.updateBatchType(id, mapper.toDTO(input));
        return ResponseEntity.ok(mapper.toModel(updated));
    }

    @Override
    public ResponseEntity<Void> deleteBatchType(Long id) {
        log.debug("DELETE /batch-types/{}", id);
        batchTypeService.deleteBatchType(id);
        return ResponseEntity.noContent().build();
    }
}
