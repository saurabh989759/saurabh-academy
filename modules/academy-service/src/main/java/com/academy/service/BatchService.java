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

import java.util.Set;
import java.util.stream.Collectors;

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
        return batchRepository.findAll(pageable).map(batchMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "batch", key = "'batch:' + #id", unless = "#result == null")
    public BatchDTO getBatchById(Long id) {
        return batchMapper.toDTO(fetchBatchOrThrow(id));
    }

    @WithLock(key = "batch:create:#{#request.name}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, allEntries = true)
    public BatchDTO createBatch(BatchDTO request) {
        log.info("Creating a new batch named: {}", request.getName());

        batchRepository.findAll().stream()
            .filter(b -> b.getName().equals(request.getName()))
            .findFirst()
            .ifPresent(conflict -> {
                throw new IllegalStateException("Batch '" + request.getName() + "' already exists");
            });

        Batch batch = batchMapper.toEntity(request);
        batch.setBatchType(resolveBatchType(request.getBatchTypeId()));

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            batch.setClasses(resolveClasses(request.getClassIds()));
        }

        Batch persisted = batchRepository.save(batch);
        log.info("Batch created, id={}", persisted.getId());
        eventProducer.publishBatchCreatedEvent(persisted);

        return batchMapper.toDTO(persisted);
    }

    @WithLock(key = "batch:update:#{#id}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, key = "'batch:' + #id", allEntries = true)
    public BatchDTO updateBatch(Long id, BatchDTO request) {
        Batch batch = batchRepository.findByIdWithLock(id)
            .orElseThrow(() -> new BatchNotFoundException(id));

        batch.setName(request.getName());
        batch.setStartMonth(request.getStartMonth());
        batch.setCurrentInstructor(request.getCurrentInstructor());

        if (request.getBatchTypeId() != null) {
            batch.setBatchType(resolveBatchType(request.getBatchTypeId()));
        }

        if (request.getClassIds() != null) {
            batch.setClasses(resolveClasses(request.getClassIds()));
        }

        return batchMapper.toDTO(batchRepository.save(batch));
    }

    @Transactional
    @CacheEvict(value = {"batch", "batches"}, key = "'batch:' + #id", allEntries = true)
    public void deleteBatch(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new BatchNotFoundException(id);
        }
        batchRepository.deleteById(id);
        log.info("Batch {} has been removed", id);
    }

    @WithLock(key = "batch:assign:class:#{#batchId}:#{#classId}", timeout = 30, maxRetries = 3, waitTimeout = 10)
    @Transactional
    @CacheEvict(value = {"batch", "batches"}, key = "'batch:' + #batchId", allEntries = true)
    public BatchDTO assignClassToBatch(Long batchId, Long classId) {
        Batch batch = batchRepository.findByIdWithLock(batchId)
            .orElseThrow(() -> new BatchNotFoundException(batchId));

        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ClassNotFoundException(classId));

        if (batch.getClasses().contains(classEntity)) {
            log.warn("Class {} is already linked to batch {}, skipping", classId, batchId);
            return batchMapper.toDTO(batch);
        }

        batch.getClasses().add(classEntity);
        return batchMapper.toDTO(batchRepository.save(batch));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Batch fetchBatchOrThrow(Long id) {
        return batchRepository.findById(id).orElseThrow(() -> new BatchNotFoundException(id));
    }

    private BatchType resolveBatchType(Long typeId) {
        return batchTypeRepository.findById(typeId)
            .orElseThrow(() -> new BatchTypeNotFoundException(typeId));
    }

    private Set<ClassEntity> resolveClasses(Set<Long> classIds) {
        return classIds.stream()
            .map(cid -> classRepository.findById(cid)
                .orElseThrow(() -> new ClassNotFoundException(cid)))
            .collect(Collectors.toSet());
    }
}
