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

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassMapper classMapper;

    @Transactional(readOnly = true)
    public List<ClassDTO> getAllClasses() {
        return classRepository.findAll().stream()
            .map(classMapper::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "class", key = "'class:' + #id", unless = "#result == null")
    public ClassDTO getClassById(Long id) {
        return classMapper.toDTO(fetchOrThrow(id));
    }

    @Transactional
    @CacheEvict(value = {"class", "classes"}, allEntries = true)
    public ClassDTO createClass(ClassDTO request) {
        ClassEntity saved = classRepository.save(classMapper.toEntity(request));
        log.info("New class scheduled with id={}", saved.getId());
        return classMapper.toDTO(saved);
    }

    @Transactional
    @CacheEvict(value = {"class", "classes"}, key = "'class:' + #id", allEntries = true)
    public ClassDTO updateClass(Long id, ClassDTO request) {
        ClassEntity target = fetchOrThrow(id);

        target.setName(request.getName());
        target.setInstructor(request.getInstructor());
        target.setDate(request.getDate());
        target.setTime(request.getTime());

        return classMapper.toDTO(classRepository.save(target));
    }

    @Transactional
    @CacheEvict(value = {"class", "classes"}, key = "'class:' + #id", allEntries = true)
    public void deleteClass(Long id) {
        if (!classRepository.existsById(id)) {
            throw new ClassNotFoundException(id);
        }
        classRepository.deleteById(id);
        log.info("Class {} removed", id);
    }

    // -------------------------------------------------------------------------

    private ClassEntity fetchOrThrow(Long id) {
        return classRepository.findById(id).orElseThrow(() -> new ClassNotFoundException(id));
    }
}
