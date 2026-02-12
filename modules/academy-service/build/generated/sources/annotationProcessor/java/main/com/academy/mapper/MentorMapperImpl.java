package com.academy.mapper;

import com.academy.dto.MentorDTO;
import com.academy.entity.Mentor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T15:35:18+0530",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.jar, environment: Java 22 (Oracle Corporation)"
)
@Component
public class MentorMapperImpl implements MentorMapper {

    @Override
    public MentorDTO toDTO(Mentor mentor) {
        if ( mentor == null ) {
            return null;
        }

        MentorDTO mentorDTO = new MentorDTO();

        mentorDTO.setId( mentor.getId() );
        mentorDTO.setName( mentor.getName() );
        mentorDTO.setCurrentCompany( mentor.getCurrentCompany() );

        return mentorDTO;
    }

    @Override
    public Mentor toEntity(MentorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Mentor mentor = new Mentor();

        mentor.setId( dto.getId() );
        mentor.setName( dto.getName() );
        mentor.setCurrentCompany( dto.getCurrentCompany() );

        return mentor;
    }
}
