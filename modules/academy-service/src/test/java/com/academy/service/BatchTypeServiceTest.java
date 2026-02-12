package com.academy.service;

import com.academy.dto.BatchTypeDTO;
import com.academy.entity.BatchType;
import com.academy.exception.BatchTypeNotFoundException;
import com.academy.mapper.BatchTypeMapper;
import com.academy.repository.BatchTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.academy.util.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for BatchTypeService
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BatchTypeService Tests")
class BatchTypeServiceTest {
    
    @Mock
    private BatchTypeRepository batchTypeRepository;
    
    @Mock
    private BatchTypeMapper batchTypeMapper;
    
    @InjectMocks
    private BatchTypeService batchTypeService;
    
    private BatchType batchTypeEntity;
    private BatchTypeDTO batchTypeDTO;
    
    @BeforeEach
    void setUp() {
        batchTypeEntity = batchTypeEntity(1L, "Full Stack Development");
        batchTypeDTO = new BatchTypeDTO();
        batchTypeDTO.setId(1L);
        batchTypeDTO.setName("Full Stack Development");
    }
    
    @Test
    @DisplayName("Should return all batch types")
    void getAllBatchTypes_ReturnsAllBatchTypes() {
        // Given
        List<BatchType> batchTypes = Arrays.asList(batchTypeEntity);
        when(batchTypeRepository.findAll()).thenReturn(batchTypes);
        when(batchTypeMapper.toDTO(any(BatchType.class))).thenReturn(batchTypeDTO);
        
        // When
        List<BatchTypeDTO> result = batchTypeService.getAllBatchTypes();
        
        // Then
        assertThat(result).hasSize(1);
        verify(batchTypeRepository).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no batch types")
    void getAllBatchTypes_WhenEmpty_ReturnsEmptyList() {
        // Given
        when(batchTypeRepository.findAll()).thenReturn(List.of());
        
        // When
        List<BatchTypeDTO> result = batchTypeService.getAllBatchTypes();
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should return batch type by id when found")
    void getBatchTypeById_WhenExists_ReturnsBatchType() {
        // Given
        Long id = 1L;
        when(batchTypeRepository.findById(id)).thenReturn(Optional.of(batchTypeEntity));
        when(batchTypeMapper.toDTO(batchTypeEntity)).thenReturn(batchTypeDTO);
        
        // When
        BatchTypeDTO result = batchTypeService.getBatchTypeById(id);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Should throw BatchTypeNotFoundException when not found")
    void getBatchTypeById_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(batchTypeRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> batchTypeService.getBatchTypeById(id))
            .isInstanceOf(BatchTypeNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should create batch type successfully")
    void createBatchType_WhenValid_ReturnsCreatedBatchType() {
        // Given
        BatchTypeDTO inputDTO = new BatchTypeDTO();
        inputDTO.setName("New Batch Type");
        BatchType newBatchType = batchTypeEntity(null, "New Batch Type");
        BatchType savedBatchType = batchTypeEntity(2L, "New Batch Type");
        BatchTypeDTO savedDTO = new BatchTypeDTO();
        savedDTO.setId(2L);
        savedDTO.setName("New Batch Type");
        
        when(batchTypeMapper.toEntity(inputDTO)).thenReturn(newBatchType);
        when(batchTypeRepository.save(any(BatchType.class))).thenReturn(savedBatchType);
        when(batchTypeMapper.toDTO(savedBatchType)).thenReturn(savedDTO);
        
        // When
        BatchTypeDTO result = batchTypeService.createBatchType(inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        verify(batchTypeRepository).save(any(BatchType.class));
    }
}

