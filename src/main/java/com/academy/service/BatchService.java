package com.academy.service;

import com.academy.dto.BatchDTO;
import com.academy.entity.Batch;
import com.academy.entity.BatchType;
import com.academy.entity.ClassEntity;
import com.academy.exception.BatchNotFoundException;
import com.academy.exception.BatchTypeNotFoundException;
import com.academy.exception.ClassNotFoundException;
import com.academy.kafka.BatchEventProducer;
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
    
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, allEntries = true)
    public BatchDTO createBatch(BatchDTO dto) {
        log.info("Creating batch: {}", dto.getName());
        
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
        
        BatchDTO result = batchMapper.toDTO(saved);
        
        return result;
    }
    
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, key = "'batch:' + #id", allEntries = true)
    public BatchDTO updateBatch(Long id, BatchDTO dto) {
        Batch batch = batchRepository.findById(id)
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
        BatchDTO result = batchMapper.toDTO(updated);
        
        return result;
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
    
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, key = "'batch:' + #batchId", allEntries = true)
    public BatchDTO assignClassToBatch(Long batchId, Long classId) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new BatchNotFoundException(batchId));
        
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ClassNotFoundException(classId));
        
        batch.getClasses().add(classEntity);
        Batch updated = batchRepository.save(batch);
        BatchDTO result = batchMapper.toDTO(updated);
        
        return result;
    }
}

