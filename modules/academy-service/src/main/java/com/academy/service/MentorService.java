package com.academy.service;

import com.academy.dto.MentorDTO;
import com.academy.entity.Mentor;
import com.academy.exception.MentorNotFoundException;
import com.academy.mapper.MentorMapper;
import com.academy.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Mentor operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MentorService {
    
    private final MentorRepository mentorRepository;
    private final MentorMapper mentorMapper;
    
    @Transactional(readOnly = true)
    @Cacheable(value = "mentors", key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<MentorDTO> getAllMentors() {
        return mentorRepository.findAll().stream()
            .map(mentorMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "mentor", key = "'mentor:' + #id", unless = "#result == null")
    public MentorDTO getMentorById(Long id) {
        Mentor mentor = mentorRepository.findById(id)
            .orElseThrow(() -> new MentorNotFoundException(id));
        return mentorMapper.toDTO(mentor);
    }
    
    @Transactional
    @CacheEvict(value = {"mentor", "mentors"}, allEntries = true)
    public MentorDTO createMentor(MentorDTO dto) {
        Mentor mentor = mentorMapper.toEntity(dto);
        Mentor saved = mentorRepository.save(mentor);
        log.info("Mentor created with id: {}", saved.getId());
        return mentorMapper.toDTO(saved);
    }
    
    @Transactional
    @CacheEvict(value = {"mentor", "mentors"}, key = "'mentor:' + #id", allEntries = true)
    public MentorDTO updateMentor(Long id, MentorDTO dto) {
        Mentor mentor = mentorRepository.findById(id)
            .orElseThrow(() -> new MentorNotFoundException(id));
        
        mentor.setName(dto.getName());
        mentor.setCurrentCompany(dto.getCurrentCompany());
        
        Mentor updated = mentorRepository.save(mentor);
        return mentorMapper.toDTO(updated);
    }
    
    @Transactional
    @CacheEvict(value = {"mentor", "mentors"}, key = "'mentor:' + #id", allEntries = true)
    public void deleteMentor(Long id) {
        if (!mentorRepository.existsById(id)) {
            throw new MentorNotFoundException(id);
        }
        mentorRepository.deleteById(id);
        log.info("Mentor deleted with id: {}", id);
    }
}
