package com.academy.controller;

import com.academy.dto.MentorDTO;
import com.academy.generated.api.MentorsApi;
import com.academy.generated.model.Mentor;
import com.academy.generated.model.MentorInput;
import com.academy.mapper.ApiModelMapper;
import com.academy.service.MentorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller implementing generated MentorsApi interface
 * Uses generated request/response models from OpenAPI
 * Directly delegates to MentorService
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class MentorController implements MentorsApi {
    
    private final MentorService mentorService;
    private final ApiModelMapper apiModelMapper;
    
    @Override
    public ResponseEntity<Mentor> createMentor(MentorInput mentorInput) {
        log.debug("Creating mentor: {}", mentorInput.getName());
        MentorDTO dto = apiModelMapper.toDTO(mentorInput);
        MentorDTO created = mentorService.createMentor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiModelMapper.toModel(created));
    }
    
    @Override
    public ResponseEntity<Void> deleteMentor(Long id) {
        log.debug("Deleting mentor: {}", id);
        mentorService.deleteMentor(id);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<List<Mentor>> getAllMentors() {
        log.debug("Getting all mentors");
        List<MentorDTO> dtos = mentorService.getAllMentors();
        return ResponseEntity.ok(apiModelMapper.toMentorModelList(dtos));
    }
    
    @Override
    public ResponseEntity<Mentor> getMentorById(Long id) {
        log.debug("Getting mentor by id: {}", id);
        MentorDTO dto = mentorService.getMentorById(id);
        return ResponseEntity.ok(apiModelMapper.toModel(dto));
    }
    
    @Override
    public ResponseEntity<Mentor> updateMentor(Long id, MentorInput mentorInput) {
        log.debug("Updating mentor: {}", id);
        MentorDTO dto = apiModelMapper.toDTO(mentorInput);
        MentorDTO updated = mentorService.updateMentor(id, dto);
        return ResponseEntity.ok(apiModelMapper.toModel(updated));
    }
}

