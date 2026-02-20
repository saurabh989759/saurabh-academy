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

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorService {

    private final MentorRepository mentorRepository;
    private final MentorMapper mentorMapper;

    @Transactional(readOnly = true)
    public List<MentorDTO> getAllMentors() {
        return mentorRepository.findAll().stream()
            .map(mentorMapper::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "mentor", key = "'mentor:' + #id", unless = "#result == null")
    public MentorDTO getMentorById(Long id) {
        return mentorMapper.toDTO(fetchOrThrow(id));
    }

    @Transactional
    @CacheEvict(value = {"mentor", "mentors"}, allEntries = true)
    public MentorDTO createMentor(MentorDTO request) {
        Mentor persisted = mentorRepository.save(mentorMapper.toEntity(request));
        log.info("Mentor onboarded with id={}", persisted.getId());
        return mentorMapper.toDTO(persisted);
    }

    @Transactional
    @CacheEvict(value = {"mentor", "mentors"}, key = "'mentor:' + #id", allEntries = true)
    public MentorDTO updateMentor(Long id, MentorDTO request) {
        Mentor mentor = fetchOrThrow(id);
        mentor.setName(request.getName());
        mentor.setCurrentCompany(request.getCurrentCompany());
        return mentorMapper.toDTO(mentorRepository.save(mentor));
    }

    @Transactional
    @CacheEvict(value = {"mentor", "mentors"}, key = "'mentor:' + #id", allEntries = true)
    public void deleteMentor(Long id) {
        if (!mentorRepository.existsById(id)) {
            throw new MentorNotFoundException(id);
        }
        mentorRepository.deleteById(id);
        log.info("Mentor {} removed", id);
    }

    // -------------------------------------------------------------------------

    private Mentor fetchOrThrow(Long id) {
        return mentorRepository.findById(id).orElseThrow(() -> new MentorNotFoundException(id));
    }
}
