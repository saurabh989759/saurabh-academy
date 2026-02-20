package com.academy.mapper;

import com.academy.dto.MentorSessionDTO;
import com.academy.entity.MentorSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;

@Mapper(componentModel = "spring", imports = Timestamp.class)
public interface MentorSessionMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(target = "time", expression = "java(session.getTime().toInstant())")
    MentorSessionDTO toDTO(MentorSession session);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "time", expression = "java(Timestamp.from(dto.getTime()))")
    MentorSession toEntity(MentorSessionDTO dto);
}
