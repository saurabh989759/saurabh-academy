package com.academy.mapper;

import com.academy.dto.MentorDTO;
import com.academy.entity.Mentor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MentorMapper {

    MentorDTO toDTO(Mentor mentor);

    Mentor toEntity(MentorDTO dto);
}
