package com.academy.mapper;

import com.academy.dto.BatchTypeDTO;
import com.academy.entity.BatchType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T15:35:18+0530",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.jar, environment: Java 22 (Oracle Corporation)"
)
@Component
public class BatchTypeMapperImpl implements BatchTypeMapper {

    @Override
    public BatchTypeDTO toDTO(BatchType batchType) {
        if ( batchType == null ) {
            return null;
        }

        BatchTypeDTO batchTypeDTO = new BatchTypeDTO();

        batchTypeDTO.setId( batchType.getId() );
        batchTypeDTO.setName( batchType.getName() );

        return batchTypeDTO;
    }

    @Override
    public BatchType toEntity(BatchTypeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        BatchType batchType = new BatchType();

        batchType.setId( dto.getId() );
        batchType.setName( dto.getName() );

        return batchType;
    }
}
