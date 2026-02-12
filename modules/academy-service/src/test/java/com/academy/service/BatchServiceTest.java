package com.academy.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.academy.util.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for BatchService
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BatchService Tests")
class BatchServiceTest {
    
    @Mock
    private BatchRepository batchRepository;
    
    @Mock
    private BatchTypeRepository batchTypeRepository;
    
    @Mock
    private ClassRepository classRepository;
    
    @Mock
    private BatchMapper batchMapper;
    
    @Mock
    private BatchEventProducer eventProducer;
    
    @InjectMocks
    private BatchService batchService;
    
    private Batch batchEntity;
    private BatchDTO batchDTO;
    private BatchType batchTypeEntity;
    private ClassEntity classEntity;
    
    @BeforeEach
    void setUp() {
        batchTypeEntity = batchTypeEntity(1L, "Full Stack Development");
        batchEntity = batchEntity(1L, "Test Batch");
        batchEntity.setBatchType(batchTypeEntity);
        batchDTO = batchDTO().id(1L).name("Test Batch").batchTypeId(1L).build();
        classEntity = classEntity(1L, "Test Class");
    }
    
    // ========== getAllBatches Tests ==========
    
    @Test
    @DisplayName("Should return paginated batches")
    void getAllBatches_WithPageable_ReturnsPaginatedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Batch> batchPage = new PageImpl<>(Arrays.asList(batchEntity));
        when(batchRepository.findAll(pageable)).thenReturn(batchPage);
        when(batchMapper.toDTO(any(Batch.class))).thenReturn(batchDTO);
        
        // When
        Page<BatchDTO> result = batchService.getAllBatches(pageable);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(batchRepository).findAll(pageable);
    }
    
    // ========== getBatchById Tests ==========
    
    @Test
    @DisplayName("Should return batch when found")
    void getBatchById_WhenExists_ReturnsBatch() {
        // Given
        Long id = 1L;
        when(batchRepository.findById(id)).thenReturn(Optional.of(batchEntity));
        when(batchMapper.toDTO(batchEntity)).thenReturn(batchDTO);
        
        // When
        BatchDTO result = batchService.getBatchById(id);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Should throw BatchNotFoundException when not found")
    void getBatchById_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(batchRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> batchService.getBatchById(id))
            .isInstanceOf(BatchNotFoundException.class);
    }
    
    // ========== createBatch Tests ==========
    
    @Test
    @DisplayName("Should create batch successfully")
    void createBatch_WhenValid_ReturnsCreatedBatch() {
        // Given
        BatchDTO inputDTO = batchDTO().name("New Batch").batchTypeId(1L).build();
        Batch newBatch = batchEntity(null, "New Batch");
        Batch savedBatch = batchEntity(2L, "New Batch");
        BatchDTO savedDTO = batchDTO().id(2L).name("New Batch").build();
        
        when(batchRepository.findAll()).thenReturn(Collections.emptyList());
        when(batchTypeRepository.findById(1L)).thenReturn(Optional.of(batchTypeEntity));
        when(batchMapper.toEntity(inputDTO)).thenReturn(newBatch);
        when(batchRepository.save(any(Batch.class))).thenReturn(savedBatch);
        when(batchMapper.toDTO(savedBatch)).thenReturn(savedDTO);
        
        // When
        BatchDTO result = batchService.createBatch(inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(batchTypeRepository).findById(1L);
        verify(batchRepository).save(any(Batch.class));
        verify(eventProducer).publishBatchCreatedEvent(savedBatch);
    }
    
    @Test
    @DisplayName("Should throw exception when batch name already exists")
    void createBatch_WhenNameExists_ThrowsException() {
        // Given
        BatchDTO inputDTO = batchDTO().name("Existing Batch").build();
        when(batchRepository.findAll()).thenReturn(Arrays.asList(batchEntity));
        
        // When/Then
        assertThatThrownBy(() -> batchService.createBatch(inputDTO))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already exists");
    }
    
    @Test
    @DisplayName("Should throw BatchTypeNotFoundException when batch type not found")
    void createBatch_WhenBatchTypeNotFound_ThrowsException() {
        // Given
        BatchDTO inputDTO = batchDTO().name("New Batch").batchTypeId(999L).build();
        when(batchRepository.findAll()).thenReturn(Collections.emptyList());
        when(batchTypeRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> batchService.createBatch(inputDTO))
            .isInstanceOf(BatchTypeNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should create batch with classes when classIds provided")
    void createBatch_WithClasses_CreatesBatchWithClasses() {
        // Given
        Set<Long> classIds = Set.of(1L, 2L);
        BatchDTO inputDTO = batchDTO().name("New Batch").batchTypeId(1L).build();
        inputDTO.setClassIds(classIds);
        ClassEntity class2 = classEntity(2L, "Class 2");
        
        when(batchRepository.findAll()).thenReturn(Collections.emptyList());
        when(batchTypeRepository.findById(1L)).thenReturn(Optional.of(batchTypeEntity));
        when(classRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(classRepository.findById(2L)).thenReturn(Optional.of(class2));
        when(batchMapper.toEntity(inputDTO)).thenReturn(batchEntity);
        when(batchRepository.save(any(Batch.class))).thenReturn(batchEntity);
        when(batchMapper.toDTO(any(Batch.class))).thenReturn(batchDTO);
        
        // When
        batchService.createBatch(inputDTO);
        
        // Then
        verify(classRepository).findById(1L);
        verify(classRepository).findById(2L);
    }
    
    @Test
    @DisplayName("Should throw ClassNotFoundException when class not found")
    void createBatch_WhenClassNotFound_ThrowsException() {
        // Given
        Set<Long> classIds = Set.of(999L);
        BatchDTO inputDTO = batchDTO().name("New Batch").batchTypeId(1L).build();
        inputDTO.setClassIds(classIds);
        
        when(batchRepository.findAll()).thenReturn(Collections.emptyList());
        when(batchTypeRepository.findById(1L)).thenReturn(Optional.of(batchTypeEntity));
        when(classRepository.findById(999L)).thenReturn(Optional.empty());
        when(batchMapper.toEntity(inputDTO)).thenReturn(batchEntity);
        
        // When/Then
        assertThatThrownBy(() -> batchService.createBatch(inputDTO))
            .isInstanceOf(ClassNotFoundException.class);
    }
    
    // ========== updateBatch Tests ==========
    
    @Test
    @DisplayName("Should update batch successfully")
    void updateBatch_WhenValid_ReturnsUpdatedBatch() {
        // Given
        Long id = 1L;
        BatchDTO inputDTO = batchDTO().id(id).name("Updated Batch").build();
        BatchDTO updatedDTO = batchDTO().id(id).name("Updated Batch").build();
        
        when(batchRepository.findByIdWithLock(id)).thenReturn(Optional.of(batchEntity));
        when(batchRepository.save(any(Batch.class))).thenReturn(batchEntity);
        when(batchMapper.toDTO(any(Batch.class))).thenReturn(updatedDTO);
        
        // When
        BatchDTO result = batchService.updateBatch(id, inputDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(batchRepository).findByIdWithLock(id);
    }
    
    @Test
    @DisplayName("Should throw BatchNotFoundException when batch not found")
    void updateBatch_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        BatchDTO inputDTO = batchDTO().build();
        when(batchRepository.findByIdWithLock(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> batchService.updateBatch(id, inputDTO))
            .isInstanceOf(BatchNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should update batch type when batchTypeId provided")
    void updateBatch_WhenBatchTypeIdProvided_UpdatesBatchType() {
        // Given
        Long id = 1L;
        Long newBatchTypeId = 2L;
        BatchDTO inputDTO = batchDTO().id(id).batchTypeId(newBatchTypeId).build();
        BatchType newBatchType = batchTypeEntity(2L, "Data Science");
        
        when(batchRepository.findByIdWithLock(id)).thenReturn(Optional.of(batchEntity));
        when(batchTypeRepository.findById(newBatchTypeId)).thenReturn(Optional.of(newBatchType));
        when(batchRepository.save(any(Batch.class))).thenReturn(batchEntity);
        when(batchMapper.toDTO(any(Batch.class))).thenReturn(batchDTO);
        
        // When
        batchService.updateBatch(id, inputDTO);
        
        // Then
        verify(batchTypeRepository).findById(newBatchTypeId);
    }
    
    @Test
    @DisplayName("Should update classes when classIds provided")
    void updateBatch_WhenClassIdsProvided_UpdatesClasses() {
        // Given
        Long id = 1L;
        Set<Long> classIds = Set.of(1L);
        BatchDTO inputDTO = batchDTO().id(id).build();
        inputDTO.setClassIds(classIds);
        
        when(batchRepository.findByIdWithLock(id)).thenReturn(Optional.of(batchEntity));
        when(classRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(batchRepository.save(any(Batch.class))).thenReturn(batchEntity);
        when(batchMapper.toDTO(any(Batch.class))).thenReturn(batchDTO);
        
        // When
        batchService.updateBatch(id, inputDTO);
        
        // Then
        verify(classRepository).findById(1L);
    }
    
    // ========== deleteBatch Tests ==========
    
    @Test
    @DisplayName("Should delete batch successfully")
    void deleteBatch_WhenExists_DeletesBatch() {
        // Given
        Long id = 1L;
        when(batchRepository.existsById(id)).thenReturn(true);
        
        // When
        batchService.deleteBatch(id);
        
        // Then
        verify(batchRepository).existsById(id);
        verify(batchRepository).deleteById(id);
    }
    
    @Test
    @DisplayName("Should throw BatchNotFoundException when batch not found for deletion")
    void deleteBatch_WhenNotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(batchRepository.existsById(id)).thenReturn(false);
        
        // When/Then
        assertThatThrownBy(() -> batchService.deleteBatch(id))
            .isInstanceOf(BatchNotFoundException.class);
        verify(batchRepository, never()).deleteById(any());
    }
    
    // ========== assignClassToBatch Tests ==========
    
    @Test
    @DisplayName("Should assign class to batch successfully")
    void assignClassToBatch_WhenValid_ReturnsUpdatedBatch() {
        // Given
        Long batchId = 1L;
        Long classId = 1L;
        batchEntity.setClasses(new HashSet<>());
        
        when(batchRepository.findByIdWithLock(batchId)).thenReturn(Optional.of(batchEntity));
        when(classRepository.findById(classId)).thenReturn(Optional.of(classEntity));
        when(batchRepository.save(any(Batch.class))).thenReturn(batchEntity);
        when(batchMapper.toDTO(any(Batch.class))).thenReturn(batchDTO);
        
        // When
        BatchDTO result = batchService.assignClassToBatch(batchId, classId);
        
        // Then
        assertThat(result).isNotNull();
        verify(batchRepository).findByIdWithLock(batchId);
        verify(classRepository).findById(classId);
    }
    
    @Test
    @DisplayName("Should return batch when class already assigned")
    void assignClassToBatch_WhenAlreadyAssigned_ReturnsBatch() {
        // Given
        Long batchId = 1L;
        Long classId = 1L;
        batchEntity.setClasses(new HashSet<>(Arrays.asList(classEntity)));
        
        when(batchRepository.findByIdWithLock(batchId)).thenReturn(Optional.of(batchEntity));
        when(classRepository.findById(classId)).thenReturn(Optional.of(classEntity));
        when(batchMapper.toDTO(any(Batch.class))).thenReturn(batchDTO);
        
        // When
        BatchDTO result = batchService.assignClassToBatch(batchId, classId);
        
        // Then
        assertThat(result).isNotNull();
        verify(batchRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw BatchNotFoundException when batch not found")
    void assignClassToBatch_WhenBatchNotFound_ThrowsException() {
        // Given
        Long batchId = 999L;
        Long classId = 1L;
        when(batchRepository.findByIdWithLock(batchId)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> batchService.assignClassToBatch(batchId, classId))
            .isInstanceOf(BatchNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should throw ClassNotFoundException when class not found")
    void assignClassToBatch_WhenClassNotFound_ThrowsException() {
        // Given
        Long batchId = 1L;
        Long classId = 999L;
        when(batchRepository.findByIdWithLock(batchId)).thenReturn(Optional.of(batchEntity));
        when(classRepository.findById(classId)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> batchService.assignClassToBatch(batchId, classId))
            .isInstanceOf(ClassNotFoundException.class);
    }
}

