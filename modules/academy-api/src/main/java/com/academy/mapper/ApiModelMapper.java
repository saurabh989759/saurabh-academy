package com.academy.mapper;

import com.academy.dto.BatchDTO;
import com.academy.dto.BatchTypeDTO;
import com.academy.dto.ClassDTO;
import com.academy.dto.MentorDTO;
import com.academy.dto.MentorSessionDTO;
import com.academy.dto.StudentDTO;
import com.academy.generated.model.Batch;
import com.academy.generated.model.BatchInput;
import com.academy.generated.model.BatchType;
import com.academy.generated.model.BatchTypeInput;
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

@Mapper(componentModel = "spring")
public interface ApiModelMapper {

    // ── Students ─────────────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "buddyId", ignore = true)
    StudentDTO toDTO(StudentInput input);

    @Mapping(target = "buddyId", ignore = true)
    Student toModel(StudentDTO dto);

    List<Student> toStudentModelList(List<StudentDTO> dtos);

    default PageStudent toPageStudent(Page<StudentDTO> page) {
        PageStudent result = new PageStudent();
        result.setContent(page.getContent().stream().map(this::toModel).toList());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setSize(page.getSize());
        result.setNumber(page.getNumber());
        result.setFirst(page.isFirst());
        result.setLast(page.isLast());
        return result;
    }

    // ── Batches ───────────────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batchTypeName", ignore = true)
    @Mapping(target = "classIds", expression = "java(input.getClassIds() != null ? new java.util.HashSet<>(input.getClassIds()) : null)")
    BatchDTO toDTO(BatchInput input);

    @Mapping(target = "classIds", expression = "java(dto.getClassIds() != null ? new java.util.ArrayList<>(dto.getClassIds()) : null)")
    Batch toModel(BatchDTO dto);

    default PageBatch toPageBatch(Page<BatchDTO> page) {
        PageBatch result = new PageBatch();
        result.setContent(page.getContent().stream().map(this::toModel).toList());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setSize(page.getSize());
        result.setNumber(page.getNumber());
        result.setFirst(page.isFirst());
        result.setLast(page.isLast());
        return result;
    }

    // ── Classes ───────────────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", expression = "java(input.getTime() != null ? java.time.LocalTime.parse(input.getTime()) : null)")
    ClassDTO toDTO(ClassInput input);

    @Mapping(target = "time", expression = "java(dto.getTime() != null ? dto.getTime().toString() : null)")
    ModelClass toModel(ClassDTO dto);

    List<ModelClass> toClassModelList(List<ClassDTO> dtos);

    // ── Mentors ───────────────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    MentorDTO toDTO(MentorInput input);

    Mentor toModel(MentorDTO dto);

    List<Mentor> toMentorModelList(List<MentorDTO> dtos);

    // ── Batch Types ───────────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    BatchTypeDTO toDTO(BatchTypeInput input);

    BatchType toModel(BatchTypeDTO dto);

    List<BatchType> toBatchTypeModelList(List<BatchTypeDTO> dtos);

    // ── Mentor Sessions ───────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", expression = "java(input.getTime() != null ? input.getTime().toInstant() : null)")
    MentorSessionDTO toDTO(MentorSessionInput input);

    @Mapping(target = "time", expression = "java(dto.getTime() != null ? dto.getTime().atOffset(java.time.ZoneOffset.UTC) : null)")
    MentorSession toModel(MentorSessionDTO dto);

    List<MentorSession> toMentorSessionModelList(List<MentorSessionDTO> dtos);
}
