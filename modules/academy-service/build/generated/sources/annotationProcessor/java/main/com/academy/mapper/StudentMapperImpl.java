package com.academy.mapper;

import com.academy.dto.StudentDTO;
import com.academy.entity.Batch;
import com.academy.entity.Student;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T15:35:18+0530",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.jar, environment: Java 22 (Oracle Corporation)"
)
@Component
public class StudentMapperImpl implements StudentMapper {

    @Override
    public StudentDTO toDTO(Student student) {
        if ( student == null ) {
            return null;
        }

        StudentDTO studentDTO = new StudentDTO();

        studentDTO.setId( student.getId() );
        studentDTO.setName( student.getName() );
        studentDTO.setGraduationYear( student.getGraduationYear() );
        studentDTO.setUniversityName( student.getUniversityName() );
        studentDTO.setEmail( student.getEmail() );
        studentDTO.setPhoneNumber( student.getPhoneNumber() );
        studentDTO.setBatchId( studentBatchId( student ) );
        studentDTO.setBuddyId( student.getBuddyId() );

        return studentDTO;
    }

    @Override
    public Student toEntity(StudentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Student student = new Student();

        student.setId( dto.getId() );
        student.setName( dto.getName() );
        student.setGraduationYear( dto.getGraduationYear() );
        student.setUniversityName( dto.getUniversityName() );
        student.setEmail( dto.getEmail() );
        student.setPhoneNumber( dto.getPhoneNumber() );
        student.setBuddyId( dto.getBuddyId() );

        return student;
    }

    private Long studentBatchId(Student student) {
        if ( student == null ) {
            return null;
        }
        Batch batch = student.getBatch();
        if ( batch == null ) {
            return null;
        }
        Long id = batch.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
