package com.academy.controller;

import com.academy.dto.MentorSessionDTO;
import com.academy.generated.api.MentorSessionsApi;
import com.academy.generated.model.MentorSession;
import com.academy.generated.model.MentorSessionInput;
import com.academy.mapper.ApiModelMapper;
import com.academy.service.MentorSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller implementing generated MentorSessionsApi interface
 * Uses generated request/response models from OpenAPI
 * Directly delegates to MentorSessionService
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class MentorSessionController implements MentorSessionsApi {
    
    private final MentorSessionService mentorSessionService;
    private final ApiModelMapper apiModelMapper;
    
    @Override
    public ResponseEntity<MentorSession> createMentorSession(MentorSessionInput mentorSessionInput) {
        log.debug("Creating mentor session for student: {} with mentor: {}", 
            mentorSessionInput.getStudentId(), mentorSessionInput.getMentorId());
        MentorSessionDTO dto = apiModelMapper.toDTO(mentorSessionInput);
        MentorSessionDTO created = mentorSessionService.createSession(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiModelMapper.toModel(created));
    }
    
    @Override
    public ResponseEntity<Void> deleteMentorSession(Long id) {
        log.debug("Deleting mentor session: {}", id);
        mentorSessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<List<MentorSession>> getAllMentorSessions() {
        log.debug("Getting all mentor sessions");
        List<MentorSessionDTO> dtos = mentorSessionService.getAllSessions();
        return ResponseEntity.ok(apiModelMapper.toMentorSessionModelList(dtos));
    }
    
    @Override
    public ResponseEntity<MentorSession> getMentorSessionById(Long id) {
        log.debug("Getting mentor session by id: {}", id);
        MentorSessionDTO dto = mentorSessionService.getSessionById(id);
        return ResponseEntity.ok(apiModelMapper.toModel(dto));
    }
    
    @Override
    public ResponseEntity<MentorSession> updateMentorSession(Long id, MentorSessionInput mentorSessionInput) {
        log.debug("Updating mentor session: {}", id);
        MentorSessionDTO dto = apiModelMapper.toDTO(mentorSessionInput);
        MentorSessionDTO updated = mentorSessionService.updateSession(id, dto);
        return ResponseEntity.ok(apiModelMapper.toModel(updated));
    }
}

