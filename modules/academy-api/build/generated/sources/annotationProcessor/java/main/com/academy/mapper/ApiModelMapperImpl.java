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
import com.academy.generated.model.Student;
import com.academy.generated.model.StudentInput;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T15:35:21+0530",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.jar, environment: Java 22 (Oracle Corporation)"
)
@Component
public class ApiModelMapperImpl implements ApiModelMapper {

    @Override
    public StudentDTO toDTO(StudentInput input) {
        if ( input == null ) {
            return null;
        }

        StudentDTO studentDTO = new StudentDTO();

        studentDTO.setName( input.getName() );
        studentDTO.setEmail( input.getEmail() );
        studentDTO.setPhoneNumber( input.getPhoneNumber() );
        studentDTO.setGraduationYear( input.getGraduationYear() );
        studentDTO.setUniversityName( input.getUniversityName() );
        studentDTO.setBatchId( input.getBatchId() );

        return studentDTO;
    }

    @Override
    public Student toModel(StudentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Student student = new Student();

        student.setId( dto.getId() );
        student.setName( dto.getName() );
        student.setEmail( dto.getEmail() );
        student.setPhoneNumber( dto.getPhoneNumber() );
        student.setGraduationYear( dto.getGraduationYear() );
        student.setUniversityName( dto.getUniversityName() );
        student.setBatchId( dto.getBatchId() );

        return student;
    }

    @Override
    public List<Student> toStudentModelList(List<StudentDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Student> list = new ArrayList<Student>( dtos.size() );
        for ( StudentDTO studentDTO : dtos ) {
            list.add( toModel( studentDTO ) );
        }

        return list;
    }

    @Override
    public BatchDTO toDTO(BatchInput input) {
        if ( input == null ) {
            return null;
        }

        BatchDTO batchDTO = new BatchDTO();

        batchDTO.setName( input.getName() );
        batchDTO.setStartMonth( input.getStartMonth() );
        batchDTO.setCurrentInstructor( input.getCurrentInstructor() );
        batchDTO.setBatchTypeId( input.getBatchTypeId() );

        batchDTO.setClassIds( input.getClassIds() != null ? new java.util.HashSet<>(input.getClassIds()) : null );

        return batchDTO;
    }

    @Override
    public Batch toModel(BatchDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Batch batch = new Batch();

        batch.setId( dto.getId() );
        batch.setName( dto.getName() );
        batch.setStartMonth( dto.getStartMonth() );
        batch.setCurrentInstructor( dto.getCurrentInstructor() );
        batch.setBatchTypeId( dto.getBatchTypeId() );
        batch.setBatchTypeName( dto.getBatchTypeName() );

        batch.setClassIds( dto.getClassIds() != null ? new java.util.ArrayList<>(dto.getClassIds()) : null );

        return batch;
    }

    @Override
    public ClassDTO toDTO(ClassInput input) {
        if ( input == null ) {
            return null;
        }

        ClassDTO classDTO = new ClassDTO();

        classDTO.setName( input.getName() );
        classDTO.setDate( input.getDate() );
        classDTO.setInstructor( input.getInstructor() );

        classDTO.setTime( input.getTime() != null ? java.time.LocalTime.parse(input.getTime()) : null );

        return classDTO;
    }

    @Override
    public ModelClass toModel(ClassDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ModelClass modelClass = new ModelClass();

        modelClass.setId( dto.getId() );
        modelClass.setName( dto.getName() );
        modelClass.setDate( dto.getDate() );
        modelClass.setInstructor( dto.getInstructor() );

        modelClass.setTime( dto.getTime() != null ? dto.getTime().toString() : null );

        return modelClass;
    }

    @Override
    public List<ModelClass> toClassModelList(List<ClassDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<ModelClass> list = new ArrayList<ModelClass>( dtos.size() );
        for ( ClassDTO classDTO : dtos ) {
            list.add( toModel( classDTO ) );
        }

        return list;
    }

    @Override
    public MentorDTO toDTO(MentorInput input) {
        if ( input == null ) {
            return null;
        }

        MentorDTO mentorDTO = new MentorDTO();

        mentorDTO.setName( input.getName() );
        mentorDTO.setCurrentCompany( input.getCurrentCompany() );

        return mentorDTO;
    }

    @Override
    public Mentor toModel(MentorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Mentor mentor = new Mentor();

        mentor.setId( dto.getId() );
        mentor.setName( dto.getName() );
        mentor.setCurrentCompany( dto.getCurrentCompany() );

        return mentor;
    }

    @Override
    public List<Mentor> toMentorModelList(List<MentorDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Mentor> list = new ArrayList<Mentor>( dtos.size() );
        for ( MentorDTO mentorDTO : dtos ) {
            list.add( toModel( mentorDTO ) );
        }

        return list;
    }

    @Override
    public MentorSessionDTO toDTO(MentorSessionInput input) {
        if ( input == null ) {
            return null;
        }

        MentorSessionDTO mentorSessionDTO = new MentorSessionDTO();

        mentorSessionDTO.setDurationMinutes( input.getDurationMinutes() );
        mentorSessionDTO.setStudentId( input.getStudentId() );
        mentorSessionDTO.setMentorId( input.getMentorId() );
        mentorSessionDTO.setStudentRating( input.getStudentRating() );
        mentorSessionDTO.setMentorRating( input.getMentorRating() );

        mentorSessionDTO.setTime( input.getTime() != null ? input.getTime().toInstant() : null );

        return mentorSessionDTO;
    }

    @Override
    public MentorSession toModel(MentorSessionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        MentorSession mentorSession = new MentorSession();

        mentorSession.setId( dto.getId() );
        mentorSession.setDurationMinutes( dto.getDurationMinutes() );
        mentorSession.setStudentId( dto.getStudentId() );
        mentorSession.setMentorId( dto.getMentorId() );
        mentorSession.setStudentRating( dto.getStudentRating() );
        mentorSession.setMentorRating( dto.getMentorRating() );

        mentorSession.setTime( dto.getTime() != null ? dto.getTime().atOffset(java.time.ZoneOffset.UTC) : null );

        return mentorSession;
    }

    @Override
    public List<MentorSession> toMentorSessionModelList(List<MentorSessionDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<MentorSession> list = new ArrayList<MentorSession>( dtos.size() );
        for ( MentorSessionDTO mentorSessionDTO : dtos ) {
            list.add( toModel( mentorSessionDTO ) );
        }

        return list;
    }
}
