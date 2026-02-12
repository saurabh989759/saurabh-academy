package com.academy.mapper;

import com.academy.dto.ClassDTO;
import com.academy.entity.ClassEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for ClassEntity
 */
@Mapper(componentModel = "spring")
public interface ClassMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "time", target = "time")
    @Mapping(source = "instructor", target = "instructor")
    ClassDTO toDTO(ClassEntity classEntity);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "time", target = "time")
    @Mapping(source = "instructor", target = "instructor")
    @Mapping(target = "batches", ignore = true)
    ClassEntity toEntity(ClassDTO dto);
}

