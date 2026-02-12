package com.academy.service;

import com.academy.annotation.WithLock;
import com.academy.dto.BatchDTO;
import com.academy.entity.Batch;
import com.academy.entity.BatchType;
import com.academy.entity.ClassEntity;
import com.academy.exception.BatchNotFoundException;
import com.academy.exception.BatchTypeNotFoundException;
import com.academy.exception.ClassNotFoundException;
import com.academy.kafka.producer.BatchEventProducer;
import com.academy.mapper.BatchMapper;
import com.academy.repository.BatchRepository;
import com.academy.repository.BatchTypeRepository;
import com.academy.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Service for Batch operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {
    
    private final BatchRepository batchRepository;
    private final BatchTypeRepository batchTypeRepository;
    private final ClassRepository classRepository;
    private final BatchMapper batchMapper;
    private final BatchEventProducer eventProducer;
    
    @Transactional(readOnly = true)
    public Page<BatchDTO> getAllBatches(Pageable pageable) {
        return batchRepository.findAll(pageable)
            .map(batchMapper::toDTO);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "batch", key = "'batch:' + #id", unless = "#result == null")
    public BatchDTO getBatchById(Long id) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new BatchNotFoundException(id));
        return batchMapper.toDTO(batch);
    }
    
    @WithLock(key = "batch:create:#{#dto.name}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, allEntries = true)
    public BatchDTO createBatch(BatchDTO dto) {
        log.info("Creating batch: {}", dto.getName());
        
        // Check if batch with same name already exists (lock is automatically acquired by @WithLock)
        batchRepository.findAll().stream()
            .filter(b -> b.getName().equals(dto.getName()))
            .findFirst()
            .ifPresent(existing -> {
                throw new IllegalStateException("Batch with name " + dto.getName() + " already exists");
            });
        
        Batch batch = batchMapper.toEntity(dto);
        
        BatchType batchType = batchTypeRepository.findById(dto.getBatchTypeId())
            .orElseThrow(() -> new BatchTypeNotFoundException(dto.getBatchTypeId()));
        batch.setBatchType(batchType);
        
        if (dto.getClassIds() != null && !dto.getClassIds().isEmpty()) {
            batch.setClasses(dto.getClassIds().stream()
                .map(classId -> classRepository.findById(classId)
                    .orElseThrow(() -> new ClassNotFoundException(classId)))
                .collect(Collectors.toSet()));
        }
        
        Batch saved = batchRepository.save(batch);
        log.info("Batch created with id: {}", saved.getId());
        
        eventProducer.publishBatchCreatedEvent(saved);
        
        return batchMapper.toDTO(saved);
    }
    
    @WithLock(key = "batch:update:#{#id}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, key = "'batch:' + #id", allEntries = true)
    public BatchDTO updateBatch(Long id, BatchDTO dto) {
        // Use pessimistic lock to prevent concurrent updates
        Batch batch = batchRepository.findByIdWithLock(id)
            .orElseThrow(() -> new BatchNotFoundException(id));
        
        batch.setName(dto.getName());
        batch.setStartMonth(dto.getStartMonth());
        batch.setCurrentInstructor(dto.getCurrentInstructor());
        
        if (dto.getBatchTypeId() != null) {
            BatchType batchType = batchTypeRepository.findById(dto.getBatchTypeId())
                .orElseThrow(() -> new BatchTypeNotFoundException(dto.getBatchTypeId()));
            batch.setBatchType(batchType);
        }
        
        if (dto.getClassIds() != null) {
            batch.setClasses(dto.getClassIds().stream()
                .map(classId -> classRepository.findById(classId)
                    .orElseThrow(() -> new ClassNotFoundException(classId)))
                .collect(Collectors.toSet()));
        }
        
        Batch updated = batchRepository.save(batch);
        return batchMapper.toDTO(updated);
    }
    
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, key = "'batch:' + #id", allEntries = true)
    public void deleteBatch(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new BatchNotFoundException(id);
        }
        batchRepository.deleteById(id);
        log.info("Batch deleted with id: {}", id);
    }
    
    @WithLock(key = "batch:assign:class:#{#batchId}:#{#classId}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, key = "'batch:' + #batchId", allEntries = true)
    public BatchDTO assignClassToBatch(Long batchId, Long classId) {
        // Use pessimistic lock + custom distributed lock for class assignment
        Batch batch = batchRepository.findByIdWithLock(batchId)
            .orElseThrow(() -> new BatchNotFoundException(batchId));
        
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ClassNotFoundException(classId));
        
        // Check if class is already assigned
        if (batch.getClasses().contains(classEntity)) {
            log.warn("Class {} already assigned to batch {}", classId, batchId);
            return batchMapper.toDTO(batch);
        }
        
        batch.getClasses().add(classEntity);
        Batch updated = batchRepository.save(batch);
        
        return batchMapper.toDTO(updated);
    }
}

