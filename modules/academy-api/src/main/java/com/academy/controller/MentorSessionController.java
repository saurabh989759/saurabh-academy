package com.academy.controller;

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

@RestController
@RequiredArgsConstructor
@Slf4j
public class MentorSessionController implements MentorSessionsApi {

    private final MentorSessionService mentorSessionService;
    private final ApiModelMapper mapper;

    @Override
    public ResponseEntity<MentorSession> createMentorSession(MentorSessionInput input) {
        log.debug("POST /mentor-sessions — student={} mentor={}", input.getStudentId(), input.getMentorId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toModel(mentorSessionService.createSession(mapper.toDTO(input))));
    }

    @Override
    public ResponseEntity<MentorSession> updateMentorSession(Long id, MentorSessionInput input) {
        log.debug("PUT /mentor-sessions/{}", id);
        return ResponseEntity.ok(mapper.toModel(mentorSessionService.updateSession(id, mapper.toDTO(input))));
    }

    @Override
    public ResponseEntity<Void> deleteMentorSession(Long id) {
        log.debug("DELETE /mentor-sessions/{}", id);
        mentorSessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<MentorSession> getMentorSessionById(Long id) {
        log.debug("GET /mentor-sessions/{}", id);
        return ResponseEntity.ok(mapper.toModel(mentorSessionService.getSessionById(id)));
    }

    @Override
    public ResponseEntity<List<MentorSession>> getAllMentorSessions() {
        log.debug("GET /mentor-sessions");
        return ResponseEntity.ok(mapper.toMentorSessionModelList(mentorSessionService.getAllSessions()));
    }
}
