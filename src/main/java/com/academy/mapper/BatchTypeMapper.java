package com.academy.mapper;

import com.academy.dto.BatchTypeDTO;
import com.academy.entity.BatchType;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for BatchType entity
 */
@Mapper(componentModel = "spring")
public interface BatchTypeMapper {
    BatchTypeDTO toDTO(BatchType batchType);
    BatchType toEntity(BatchTypeDTO dto);
}

