package com.academy.service;

import com.academy.dto.ClassDTO;
import com.academy.entity.ClassEntity;
import com.academy.exception.ClassNotFoundException;
import com.academy.mapper.ClassMapper;
import com.academy.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Class operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClassService {
    
    private final ClassRepository classRepository;
    private final ClassMapper classMapper;
    
    @Transactional(readOnly = true)
    @Cacheable(value = "classes", key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<ClassDTO> getAllClasses() {
        return classRepository.findAll().stream()
            .map(classMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "class", key = "'class:' + #id", unless = "#result == null")
    public ClassDTO getClassById(Long id) {
        ClassEntity classEntity = classRepository.findById(id)
            .orElseThrow(() -> new ClassNotFoundException(id));
        return classMapper.toDTO(classEntity);
    }
    
    @Transactional
    @CacheEvict(value = {"class", "classes"}, allEntries = true)
    public ClassDTO createClass(ClassDTO dto) {
        ClassEntity classEntity = classMapper.toEntity(dto);
        ClassEntity saved = classRepository.save(classEntity);
        log.info("Class created with id: {}", saved.getId());
        return classMapper.toDTO(saved);
    }
    
    @Transactional
    @CacheEvict(value = {"class", "classes"}, key = "'class:' + #id", allEntries = true)
    public ClassDTO updateClass(Long id, ClassDTO dto) {
        ClassEntity classEntity = classRepository.findById(id)
            .orElseThrow(() -> new ClassNotFoundException(id));
        
        classEntity.setName(dto.getName());
        classEntity.setDate(dto.getDate());
        classEntity.setTime(dto.getTime());
        classEntity.setInstructor(dto.getInstructor());
        
        ClassEntity updated = classRepository.save(classEntity);
        return classMapper.toDTO(updated);
    }
    
    @Transactional
    @CacheEvict(value = {"class", "classes"}, key = "'class:' + #id", allEntries = true)
    public void deleteClass(Long id) {
        if (!classRepository.existsById(id)) {
            throw new ClassNotFoundException(id);
        }
        classRepository.deleteById(id);
        log.info("Class deleted with id: {}", id);
    }
}
