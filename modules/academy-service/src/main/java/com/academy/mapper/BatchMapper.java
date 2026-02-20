package com.academy.mapper;

import com.academy.dto.BatchDTO;
import com.academy.entity.Batch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class, Collections.class})
public interface BatchMapper {

    @Mapping(source = "batchType.id", target = "batchTypeId")
    @Mapping(source = "batchType.name", target = "batchTypeName")
    @Mapping(target = "classIds", expression = "java(batch.getClasses() != null ? batch.getClasses().stream().map(c -> c.getId()).collect(Collectors.toSet()) : Collections.emptySet())")
    BatchDTO toDTO(Batch batch);

    @Mapping(target = "batchType", ignore = true)
    @Mapping(target = "classes", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "version", ignore = true)
    Batch toEntity(BatchDTO dto);
}
