package com.academy.mapper;

import com.academy.dto.ClassDTO;
import com.academy.entity.ClassEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassMapper {

    ClassDTO toDTO(ClassEntity classEntity);

    @Mapping(target = "batches", ignore = true)
    ClassEntity toEntity(ClassDTO dto);
}
