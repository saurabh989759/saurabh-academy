package com.academy.mapper;

import com.academy.dto.BatchDTO;
import com.academy.dto.ClassDTO;
import com.academy.dto.MentorDTO;
import com.academy.dto.MentorSessionDTO;
import com.academy.dto.StudentDTO;
import com.academy.generated.model.Batch;
import com.academy.generated.model.BatchInput;
import com.academy.generated.model.ClassInput;
import com.academy.generated.model.Mentor;
import com.academy.generated.model.MentorInput;
import com.academy.generated.model.MentorSession;
import com.academy.generated.model.MentorSessionInput;
import com.academy.generated.model.ModelClass;
import com.academy.generated.model.PageBatch;
import com.academy.generated.model.PageStudent;
import com.academy.generated.model.Student;
import com.academy.generated.model.StudentInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * MapStruct mapper for converting between generated OpenAPI models and service DTOs
 */
@Mapper(componentModel = "spring")
public interface ApiModelMapper {
    
    // ========== Student Mappings ==========
    
    @Mapping(target = "id", ignore = true)
    StudentDTO toDTO(StudentInput input);
    
    Student toModel(StudentDTO dto);
    
    List<Student> toStudentModelList(List<StudentDTO> dtos);
    
    default PageStudent toPageStudent(Page<StudentDTO> dtoPage) {
        PageStudent pageStudent = new PageStudent();
        pageStudent.setContent(dtoPage.getContent().stream()
            .map(this::toModel)
            .toList());
        pageStudent.setTotalElements(dtoPage.getTotalElements());
        pageStudent.setTotalPages(dtoPage.getTotalPages());
        pageStudent.setSize(dtoPage.getSize());
        pageStudent.setNumber(dtoPage.getNumber());
        pageStudent.setFirst(dtoPage.isFirst());
        pageStudent.setLast(dtoPage.isLast());
        return pageStudent;
    }
    
    // ========== Batch Mappings ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batchTypeName", ignore = true)
    @Mapping(target = "classIds", expression = "java(input.getClassIds() != null ? new java.util.HashSet<>(input.getClassIds()) : null)")
    BatchDTO toDTO(BatchInput input);
    
    @Mapping(target = "classIds", expression = "java(dto.getClassIds() != null ? new java.util.ArrayList<>(dto.getClassIds()) : null)")
    Batch toModel(BatchDTO dto);
    
    default PageBatch toPageBatch(Page<BatchDTO> dtoPage) {
        PageBatch pageBatch = new PageBatch();
        pageBatch.setContent(dtoPage.getContent().stream()
            .map(this::toModel)
            .toList());
        pageBatch.setTotalElements(dtoPage.getTotalElements());
        pageBatch.setTotalPages(dtoPage.getTotalPages());
        pageBatch.setSize(dtoPage.getSize());
        pageBatch.setNumber(dtoPage.getNumber());
        pageBatch.setFirst(dtoPage.isFirst());
        pageBatch.setLast(dtoPage.isLast());
        return pageBatch;
    }
    
    // ========== Class Mappings ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", expression = "java(input.getTime() != null ? java.time.LocalTime.parse(input.getTime()) : null)")
    ClassDTO toDTO(ClassInput input);
    
    @Mapping(target = "time", expression = "java(dto.getTime() != null ? dto.getTime().toString() : null)")
    ModelClass toModel(ClassDTO dto);
    
    List<ModelClass> toClassModelList(List<ClassDTO> dtos);
    
    
    // ========== Mentor Mappings ==========
    
    @Mapping(target = "id", ignore = true)
    MentorDTO toDTO(MentorInput input);
    
    Mentor toModel(MentorDTO dto);
    
    List<Mentor> toMentorModelList(List<MentorDTO> dtos);
    
    // ========== MentorSession Mappings ==========
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", expression = "java(input.getTime() != null ? input.getTime().toInstant() : null)")
    MentorSessionDTO toDTO(MentorSessionInput input);
    
    @Mapping(target = "time", expression = "java(dto.getTime() != null ? dto.getTime().atOffset(java.time.ZoneOffset.UTC) : null)")
    MentorSession toModel(MentorSessionDTO dto);
    
    List<MentorSession> toMentorSessionModelList(List<MentorSessionDTO> dtos);
}

