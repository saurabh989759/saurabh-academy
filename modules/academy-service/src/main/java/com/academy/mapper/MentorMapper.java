package com.academy.mapper;

import com.academy.dto.MentorDTO;
import com.academy.entity.Mentor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Mentor entity
 */
@Mapper(componentModel = "spring")
public interface MentorMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "currentCompany", target = "currentCompany")
    MentorDTO toDTO(Mentor mentor);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "currentCompany", target = "currentCompany")
    Mentor toEntity(MentorDTO dto);
}

