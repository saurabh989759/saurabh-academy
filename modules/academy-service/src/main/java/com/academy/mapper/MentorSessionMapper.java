package com.academy.mapper;

import com.academy.dto.MentorSessionDTO;
import com.academy.entity.MentorSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;

/**
 * MapStruct mapper for MentorSession entity
 * Report: Feature Development Process - Mapper used in "Book a Mentor Session" feature
 */
@Mapper(componentModel = "spring", imports = Timestamp.class)
public interface MentorSessionMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(target = "time", expression = "java(session.getTime().toInstant())")
    @Mapping(source = "durationMinutes", target = "durationMinutes")
    @Mapping(source = "studentRating", target = "studentRating")
    @Mapping(source = "mentorRating", target = "mentorRating")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    MentorSessionDTO toDTO(MentorSession session);
    
    @Mapping(source = "id", target = "id")
    @Mapping(target = "time", expression = "java(Timestamp.from(dto.getTime()))")
    @Mapping(source = "durationMinutes", target = "durationMinutes")
    @Mapping(source = "studentRating", target = "studentRating")
    @Mapping(source = "mentorRating", target = "mentorRating")
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "version", ignore = true) // Version is managed by JPA
    MentorSession toEntity(MentorSessionDTO dto);
}

