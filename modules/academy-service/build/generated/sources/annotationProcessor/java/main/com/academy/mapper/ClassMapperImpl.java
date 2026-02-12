package com.academy.mapper;

import com.academy.dto.ClassDTO;
import com.academy.entity.ClassEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T15:35:18+0530",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.jar, environment: Java 22 (Oracle Corporation)"
)
@Component
public class ClassMapperImpl implements ClassMapper {

    @Override
    public ClassDTO toDTO(ClassEntity classEntity) {
        if ( classEntity == null ) {
            return null;
        }

        ClassDTO classDTO = new ClassDTO();

        classDTO.setId( classEntity.getId() );
        classDTO.setName( classEntity.getName() );
        classDTO.setDate( classEntity.getDate() );
        classDTO.setTime( classEntity.getTime() );
        classDTO.setInstructor( classEntity.getInstructor() );

        return classDTO;
    }

    @Override
    public ClassEntity toEntity(ClassDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ClassEntity classEntity = new ClassEntity();

        classEntity.setId( dto.getId() );
        classEntity.setName( dto.getName() );
        classEntity.setDate( dto.getDate() );
        classEntity.setTime( dto.getTime() );
        classEntity.setInstructor( dto.getInstructor() );

        return classEntity;
    }
}
