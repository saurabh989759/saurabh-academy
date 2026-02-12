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
    
    @Mapping(target = "time", expression = "java(session.getTime().toInstant())")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "mentorId", source = "mentor.id")
    MentorSessionDTO toDTO(MentorSession session);
    
    @Mapping(target = "time", expression = "java(Timestamp.from(dto.getTime()))")
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    MentorSession toEntity(MentorSessionDTO dto);
}

