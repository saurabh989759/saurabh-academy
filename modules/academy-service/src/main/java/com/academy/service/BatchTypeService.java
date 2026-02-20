package com.academy.service;

import com.academy.dto.BatchTypeDTO;
import com.academy.entity.BatchType;
import com.academy.exception.BatchTypeNotFoundException;
import com.academy.mapper.BatchTypeMapper;
import com.academy.repository.BatchTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchTypeService {

    private final BatchTypeRepository batchTypeRepository;
    private final BatchTypeMapper batchTypeMapper;

    @Transactional(readOnly = true)
    public List<BatchTypeDTO> getAllBatchTypes() {
        return batchTypeRepository.findAll().stream()
            .map(batchTypeMapper::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "batchType", key = "'batchType:' + #id", unless = "#result == null")
    public BatchTypeDTO getBatchTypeById(Long id) {
        return batchTypeMapper.toDTO(fetchOrThrow(id));
    }

    @Transactional
    @CacheEvict(value = {"batchType", "batchTypes"}, allEntries = true)
    public BatchTypeDTO createBatchType(BatchTypeDTO request) {
        BatchType persisted = batchTypeRepository.save(batchTypeMapper.toEntity(request));
        log.info("BatchType created with id={}", persisted.getId());
        return batchTypeMapper.toDTO(persisted);
    }

    @Transactional
    @CacheEvict(value = {"batchType", "batchTypes"}, allEntries = true)
    public BatchTypeDTO updateBatchType(Long id, BatchTypeDTO request) {
        BatchType existing = fetchOrThrow(id);
        existing.setName(request.getName());
        BatchType persisted = batchTypeRepository.save(existing);
        log.info("BatchType updated id={}", id);
        return batchTypeMapper.toDTO(persisted);
    }

    @Transactional
    @CacheEvict(value = {"batchType", "batchTypes"}, allEntries = true)
    public void deleteBatchType(Long id) {
        fetchOrThrow(id);
        batchTypeRepository.deleteById(id);
        log.info("BatchType deleted id={}", id);
    }

    // -------------------------------------------------------------------------

    private BatchType fetchOrThrow(Long id) {
        return batchTypeRepository.findById(id).orElseThrow(() -> new BatchTypeNotFoundException(id));
    }
}
