package com.academy.mapper;

import com.academy.dto.StudentDTO;
import com.academy.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(source = "batch.id", target = "batchId")
    StudentDTO toDTO(Student student);

    @Mapping(target = "batch", ignore = true)
    @Mapping(target = "version", ignore = true)
    Student toEntity(StudentDTO dto);
}
