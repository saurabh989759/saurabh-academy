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
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "graduationYear", target = "graduationYear")
    @Mapping(source = "universityName", target = "universityName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "batch.id", target = "batchId")
    @Mapping(source = "buddyId", target = "buddyId")
    StudentDTO toDTO(Student student);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "graduationYear", target = "graduationYear")
    @Mapping(source = "universityName", target = "universityName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "buddyId", target = "buddyId")
    @Mapping(target = "batch", ignore = true)
    @Mapping(target = "version", ignore = true) // Version is managed by JPA
    Student toEntity(StudentDTO dto);
}

