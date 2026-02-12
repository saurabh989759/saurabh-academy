package com.academy.mapper;

import com.academy.dto.MentorSessionDTO;
import com.academy.entity.Mentor;
import com.academy.entity.MentorSession;
import com.academy.entity.Student;
import java.sql.Timestamp;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T15:35:18+0530",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.jar, environment: Java 22 (Oracle Corporation)"
)
@Component
public class MentorSessionMapperImpl implements MentorSessionMapper {

    @Override
    public MentorSessionDTO toDTO(MentorSession session) {
        if ( session == null ) {
            return null;
        }

        MentorSessionDTO mentorSessionDTO = new MentorSessionDTO();

        mentorSessionDTO.setId( session.getId() );
        mentorSessionDTO.setDurationMinutes( session.getDurationMinutes() );
        mentorSessionDTO.setStudentRating( session.getStudentRating() );
        mentorSessionDTO.setMentorRating( session.getMentorRating() );
        mentorSessionDTO.setStudentId( sessionStudentId( session ) );
        mentorSessionDTO.setMentorId( sessionMentorId( session ) );

        mentorSessionDTO.setTime( session.getTime().toInstant() );

        return mentorSessionDTO;
    }

    @Override
    public MentorSession toEntity(MentorSessionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        MentorSession mentorSession = new MentorSession();

        mentorSession.setId( dto.getId() );
        mentorSession.setDurationMinutes( dto.getDurationMinutes() );
        mentorSession.setStudentRating( dto.getStudentRating() );
        mentorSession.setMentorRating( dto.getMentorRating() );

        mentorSession.setTime( Timestamp.from(dto.getTime()) );

        return mentorSession;
    }

    private Long sessionStudentId(MentorSession mentorSession) {
        if ( mentorSession == null ) {
            return null;
        }
        Student student = mentorSession.getStudent();
        if ( student == null ) {
            return null;
        }
        Long id = student.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long sessionMentorId(MentorSession mentorSession) {
        if ( mentorSession == null ) {
            return null;
        }
        Mentor mentor = mentorSession.getMentor();
        if ( mentor == null ) {
            return null;
        }
        Long id = mentor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
