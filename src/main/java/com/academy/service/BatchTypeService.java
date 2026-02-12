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
import java.util.stream.Collectors;

/**
 * Service for BatchType operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchTypeService {
    
    private final BatchTypeRepository batchTypeRepository;
    private final BatchTypeMapper batchTypeMapper;
    
    @Transactional(readOnly = true)
    @Cacheable(value = "batchTypes", key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<BatchTypeDTO> getAllBatchTypes() {
        return batchTypeRepository.findAll().stream()
            .map(batchTypeMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "batchType", key = "'batchType:' + #id", unless = "#result == null")
    public BatchTypeDTO getBatchTypeById(Long id) {
        BatchType batchType = batchTypeRepository.findById(id)
            .orElseThrow(() -> new BatchTypeNotFoundException(id));
        return batchTypeMapper.toDTO(batchType);
    }
    
    @Transactional
    @CacheEvict(value = {"batchType", "batchTypes"}, allEntries = true)
    public BatchTypeDTO createBatchType(BatchTypeDTO dto) {
        BatchType batchType = batchTypeMapper.toEntity(dto);
        BatchType saved = batchTypeRepository.save(batchType);
        log.info("BatchType created with id: {}", saved.getId());
        return batchTypeMapper.toDTO(saved);
    }
}
