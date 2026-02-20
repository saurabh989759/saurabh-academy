package com.academy.controller;

import com.academy.dto.BatchDTO;
import com.academy.generated.api.BatchesApi;
import com.academy.generated.model.Batch;
import com.academy.generated.model.BatchInput;
import com.academy.generated.model.PageBatch;
import com.academy.mapper.ApiModelMapper;
import com.academy.service.BatchService;
import com.academy.service.WebSocketEventPublisher;
import com.academy.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BatchController implements BatchesApi {

    private final BatchService batchService;
    private final ApiModelMapper mapper;
    private final WebSocketEventPublisher eventPublisher;

    @Override
    public ResponseEntity<Batch> createBatch(BatchInput batchInput) {
        log.debug("POST /batches — name={}", batchInput.getName());
        BatchDTO created = batchService.createBatch(mapper.toDTO(batchInput));
        eventPublisher.publishBatchCreated(created.getId(), created.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toModel(created));
    }

    @Override
    public ResponseEntity<Batch> updateBatch(Long id, BatchInput batchInput) {
        log.debug("PUT /batches/{}", id);
        BatchDTO updated = batchService.updateBatch(id, mapper.toDTO(batchInput));
        eventPublisher.publishBatchUpdated(updated.getId(), updated.getName());
        return ResponseEntity.ok(mapper.toModel(updated));
    }

    @Override
    public ResponseEntity<Void> deleteBatch(Long id) {
        log.debug("DELETE /batches/{}", id);
        batchService.deleteBatch(id);
        eventPublisher.publishBatchDeleted(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Batch> getBatchById(Long id) {
        log.debug("GET /batches/{}", id);
        return ResponseEntity.ok(mapper.toModel(batchService.getBatchById(id)));
    }

    @Override
    public ResponseEntity<PageBatch> getAllBatches(Integer page, Integer size, String sort) {
        log.debug("GET /batches page={} size={}", page, size);
        Pageable pageable = PageableUtil.createPageable(page, size, sort);
        Page<BatchDTO> resultPage = batchService.getAllBatches(pageable);
        return ResponseEntity.ok(mapper.toPageBatch(resultPage));
    }

    @Override
    public ResponseEntity<Batch> assignClassToBatch(Long id, Long classId) {
        log.debug("POST /batches/{}/classes/{}", id, classId);
        return ResponseEntity.ok(mapper.toModel(batchService.assignClassToBatch(id, classId)));
    }
}
