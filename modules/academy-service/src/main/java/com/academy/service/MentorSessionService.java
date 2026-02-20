package com.academy.service;

import com.academy.dto.MentorSessionDTO;
import com.academy.entity.Mentor;
import com.academy.entity.MentorSession;
import com.academy.entity.Student;
import com.academy.exception.MentorNotFoundException;
import com.academy.exception.MentorSessionNotFoundException;
import com.academy.exception.StudentNotFoundException;
import com.academy.kafka.producer.MentorSessionEventProducer;
import com.academy.mapper.MentorSessionMapper;
import com.academy.repository.MentorRepository;
import com.academy.repository.MentorSessionRepository;
import com.academy.repository.StudentRepository;
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
public class MentorSessionService {

    private final MentorSessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final MentorSessionMapper sessionMapper;
    private final MentorSessionEventProducer eventProducer;

    @Transactional(readOnly = true)
    public List<MentorSessionDTO> getAllSessions() {
        return sessionRepository.findAll().stream()
            .map(sessionMapper::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "mentorSession", key = "'mentorSession:' + #id", unless = "#result == null")
    public MentorSessionDTO getSessionById(Long id) {
        return sessionMapper.toDTO(fetchOrThrow(id));
    }

    @Transactional
    @CacheEvict(value = {"mentorSession", "mentorSessions"}, allEntries = true)
    public MentorSessionDTO createSession(MentorSessionDTO request) {
        log.info("Booking session between student {} and mentor {}", request.getStudentId(), request.getMentorId());

        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new StudentNotFoundException(request.getStudentId()));

        Mentor mentor = mentorRepository.findById(request.getMentorId())
            .orElseThrow(() -> new MentorNotFoundException(request.getMentorId()));

        MentorSession session = sessionMapper.toEntity(request);
        session.setStudent(student);
        session.setMentor(mentor);

        MentorSession persisted = sessionRepository.save(session);
        log.info("Session booked with id={}", persisted.getId());
        eventProducer.publishSessionCreatedEvent(persisted);

        return sessionMapper.toDTO(persisted);
    }

    @Transactional
    @CacheEvict(value = {"mentorSession", "mentorSessions"}, key = "'mentorSession:' + #id", allEntries = true)
    public MentorSessionDTO updateSession(Long id, MentorSessionDTO request) {
        MentorSession session = fetchOrThrow(id);

        session.setTime(java.sql.Timestamp.from(request.getTime()));
        session.setDurationMinutes(request.getDurationMinutes());
        session.setStudentRating(request.getStudentRating());
        session.setMentorRating(request.getMentorRating());

        if (request.getStudentId() != null) {
            session.setStudent(studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(request.getStudentId())));
        }

        if (request.getMentorId() != null) {
            session.setMentor(mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new MentorNotFoundException(request.getMentorId())));
        }

        return sessionMapper.toDTO(sessionRepository.save(session));
    }

    @Transactional
    @CacheEvict(value = {"mentorSession", "mentorSessions"}, key = "'mentorSession:' + #id", allEntries = true)
    public void deleteSession(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new MentorSessionNotFoundException(id);
        }
        sessionRepository.deleteById(id);
        log.info("Session {} cancelled and removed", id);
    }

    // -------------------------------------------------------------------------

    private MentorSession fetchOrThrow(Long id) {
        return sessionRepository.findById(id).orElseThrow(() -> new MentorSessionNotFoundException(id));
    }
}
