package com.academy.controller;

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

@RestController
@RequiredArgsConstructor
@Slf4j
public class MentorController implements MentorsApi {

    private final MentorService mentorService;
    private final ApiModelMapper mapper;

    @Override
    public ResponseEntity<Mentor> createMentor(MentorInput mentorInput) {
        log.debug("POST /mentors — name={}", mentorInput.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toModel(mentorService.createMentor(mapper.toDTO(mentorInput))));
    }

    @Override
    public ResponseEntity<Mentor> updateMentor(Long id, MentorInput mentorInput) {
        log.debug("PUT /mentors/{}", id);
        return ResponseEntity.ok(mapper.toModel(mentorService.updateMentor(id, mapper.toDTO(mentorInput))));
    }

    @Override
    public ResponseEntity<Void> deleteMentor(Long id) {
        log.debug("DELETE /mentors/{}", id);
        mentorService.deleteMentor(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Mentor> getMentorById(Long id) {
        log.debug("GET /mentors/{}", id);
        return ResponseEntity.ok(mapper.toModel(mentorService.getMentorById(id)));
    }

    @Override
    public ResponseEntity<List<Mentor>> getAllMentors() {
        log.debug("GET /mentors");
        return ResponseEntity.ok(mapper.toMentorModelList(mentorService.getAllMentors()));
    }
}
