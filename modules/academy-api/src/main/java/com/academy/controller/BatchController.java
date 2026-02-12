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

/**
 * Controller implementing generated BatchesApi interface
 * Uses generated request/response models from OpenAPI
 * Directly delegates to BatchService
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class BatchController implements BatchesApi {
    
    private final BatchService batchService;
    private final ApiModelMapper apiModelMapper;
    private final WebSocketEventPublisher webSocketEventPublisher;
    
    @Override
    public ResponseEntity<Batch> assignClassToBatch(Long id, Long classId) {
        log.debug("Assigning class {} to batch {}", classId, id);
        BatchDTO dto = batchService.assignClassToBatch(id, classId);
        return ResponseEntity.ok(apiModelMapper.toModel(dto));
    }
    
    @Override
    public ResponseEntity<Batch> createBatch(BatchInput batchInput) {
        log.debug("Creating batch: {}", batchInput.getName());
        BatchDTO dto = apiModelMapper.toDTO(batchInput);
        BatchDTO created = batchService.createBatch(dto);
        // Publish WebSocket event
        webSocketEventPublisher.publishBatchCreated(created.getId(), created.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(apiModelMapper.toModel(created));
    }
    
    @Override
    public ResponseEntity<Void> deleteBatch(Long id) {
        log.debug("Deleting batch: {}", id);
        batchService.deleteBatch(id);
        // Publish WebSocket event
        webSocketEventPublisher.publishBatchDeleted(id);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<PageBatch> getAllBatches(Integer page, Integer size, String sort) {
        log.debug("Getting all batches, page: {}, size: {}, sort: {}", page, size, sort);
        Pageable pageable = PageableUtil.createPageable(page, size, sort);
        Page<BatchDTO> dtoPage = batchService.getAllBatches(pageable);
        return ResponseEntity.ok(apiModelMapper.toPageBatch(dtoPage));
    }
    
    @Override
    public ResponseEntity<Batch> getBatchById(Long id) {
        log.debug("Getting batch by id: {}", id);
        BatchDTO dto = batchService.getBatchById(id);
        return ResponseEntity.ok(apiModelMapper.toModel(dto));
    }
    
    @Override
    public ResponseEntity<Batch> updateBatch(Long id, BatchInput batchInput) {
        log.debug("Updating batch: {}", id);
        BatchDTO dto = apiModelMapper.toDTO(batchInput);
        BatchDTO updated = batchService.updateBatch(id, dto);
        // Publish WebSocket event
        webSocketEventPublisher.publishBatchUpdated(updated.getId(), updated.getName());
        return ResponseEntity.ok(apiModelMapper.toModel(updated));
    }
}

