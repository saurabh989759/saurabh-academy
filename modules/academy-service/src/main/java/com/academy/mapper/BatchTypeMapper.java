package com.academy.mapper;

import com.academy.dto.BatchTypeDTO;
import com.academy.entity.BatchType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for BatchType entity
 */
@Mapper(componentModel = "spring")
public interface BatchTypeMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    BatchTypeDTO toDTO(BatchType batchType);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    BatchType toEntity(BatchTypeDTO dto);
}

