package com.academy.mapper;

import com.academy.dto.StudentDTO;
import com.academy.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Student entity
 */
@Mapper(componentModel = "spring")
public interface StudentMapper {
    
    @Mapping(target = "batchId", source = "batch.id")
    StudentDTO toDTO(Student student);
    
    @Mapping(target = "batch", ignore = true)
    Student toEntity(StudentDTO dto);
}

