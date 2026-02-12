package com.academy.mapper;

import com.academy.dto.MentorDTO;
import com.academy.entity.Mentor;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Mentor entity
 */
@Mapper(componentModel = "spring")
public interface MentorMapper {
    MentorDTO toDTO(Mentor mentor);
    Mentor toEntity(MentorDTO dto);
}

