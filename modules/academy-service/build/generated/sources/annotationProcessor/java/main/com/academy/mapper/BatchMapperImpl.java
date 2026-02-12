package com.academy.mapper;

import com.academy.dto.BatchDTO;
import com.academy.entity.Batch;
import com.academy.entity.BatchType;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T15:35:18+0530",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.jar, environment: Java 22 (Oracle Corporation)"
)
@Component
public class BatchMapperImpl implements BatchMapper {

    @Override
    public BatchDTO toDTO(Batch batch) {
        if ( batch == null ) {
            return null;
        }

        BatchDTO batchDTO = new BatchDTO();

        batchDTO.setId( batch.getId() );
        batchDTO.setName( batch.getName() );
        batchDTO.setStartMonth( batch.getStartMonth() );
        batchDTO.setCurrentInstructor( batch.getCurrentInstructor() );
        batchDTO.setBatchTypeId( batchBatchTypeId( batch ) );
        batchDTO.setBatchTypeName( batchBatchTypeName( batch ) );

        batchDTO.setClassIds( batch.getClasses() != null ? batch.getClasses().stream().map(c -> c.getId()).collect(Collectors.toSet()) : Collections.emptySet() );

        return batchDTO;
    }

    @Override
    public Batch toEntity(BatchDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Batch batch = new Batch();

        batch.setId( dto.getId() );
        batch.setName( dto.getName() );
        batch.setStartMonth( dto.getStartMonth() );
        batch.setCurrentInstructor( dto.getCurrentInstructor() );

        return batch;
    }

    private Long batchBatchTypeId(Batch batch) {
        if ( batch == null ) {
            return null;
        }
        BatchType batchType = batch.getBatchType();
        if ( batchType == null ) {
            return null;
        }
        Long id = batchType.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String batchBatchTypeName(Batch batch) {
        if ( batch == null ) {
            return null;
        }
        BatchType batchType = batch.getBatchType();
        if ( batchType == null ) {
            return null;
        }
        String name = batchType.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
