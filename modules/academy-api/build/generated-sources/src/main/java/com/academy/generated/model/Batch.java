package com.academy.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Batch
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class Batch {

  private Long id;

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate startMonth;

  private String currentInstructor;

  private Long batchTypeId;

  private String batchTypeName;

  @Valid
  private List<Long> classIds = new ArrayList<>();

  public Batch id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Batch ID
   * @return id
  */
  
  @Schema(name = "id", description = "Batch ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Batch name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Batch name
   * @return name
  */
  
  @Schema(name = "name", example = "FSD-2024-03", description = "Batch name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Batch startMonth(LocalDate startMonth) {
    this.startMonth = startMonth;
    return this;
  }

  /**
   * Start month of the batch
   * @return startMonth
  */
  @Valid 
  @Schema(name = "startMonth", example = "Fri Mar 01 05:30:00 IST 2024", description = "Start month of the batch", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startMonth")
  public LocalDate getStartMonth() {
    return startMonth;
  }

  public void setStartMonth(LocalDate startMonth) {
    this.startMonth = startMonth;
  }

  public Batch currentInstructor(String currentInstructor) {
    this.currentInstructor = currentInstructor;
    return this;
  }

  /**
   * Current instructor name
   * @return currentInstructor
  */
  
  @Schema(name = "currentInstructor", example = "John Instructor", description = "Current instructor name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currentInstructor")
  public String getCurrentInstructor() {
    return currentInstructor;
  }

  public void setCurrentInstructor(String currentInstructor) {
    this.currentInstructor = currentInstructor;
  }

  public Batch batchTypeId(Long batchTypeId) {
    this.batchTypeId = batchTypeId;
    return this;
  }

  /**
   * Batch type ID
   * @return batchTypeId
  */
  
  @Schema(name = "batchTypeId", description = "Batch type ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("batchTypeId")
  public Long getBatchTypeId() {
    return batchTypeId;
  }

  public void setBatchTypeId(Long batchTypeId) {
    this.batchTypeId = batchTypeId;
  }

  public Batch batchTypeName(String batchTypeName) {
    this.batchTypeName = batchTypeName;
    return this;
  }

  /**
   * Batch type name
   * @return batchTypeName
  */
  
  @Schema(name = "batchTypeName", example = "Full Stack Development", description = "Batch type name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("batchTypeName")
  public String getBatchTypeName() {
    return batchTypeName;
  }

  public void setBatchTypeName(String batchTypeName) {
    this.batchTypeName = batchTypeName;
  }

  public Batch classIds(List<Long> classIds) {
    this.classIds = classIds;
    return this;
  }

  public Batch addClassIdsItem(Long classIdsItem) {
    if (this.classIds == null) {
      this.classIds = new ArrayList<>();
    }
    this.classIds.add(classIdsItem);
    return this;
  }

  /**
   * List of class IDs assigned to this batch
   * @return classIds
  */
  
  @Schema(name = "classIds", description = "List of class IDs assigned to this batch", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("classIds")
  public List<Long> getClassIds() {
    return classIds;
  }

  public void setClassIds(List<Long> classIds) {
    this.classIds = classIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Batch batch = (Batch) o;
    return Objects.equals(this.id, batch.id) &&
        Objects.equals(this.name, batch.name) &&
        Objects.equals(this.startMonth, batch.startMonth) &&
        Objects.equals(this.currentInstructor, batch.currentInstructor) &&
        Objects.equals(this.batchTypeId, batch.batchTypeId) &&
        Objects.equals(this.batchTypeName, batch.batchTypeName) &&
        Objects.equals(this.classIds, batch.classIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, startMonth, currentInstructor, batchTypeId, batchTypeName, classIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Batch {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    startMonth: ").append(toIndentedString(startMonth)).append("\n");
    sb.append("    currentInstructor: ").append(toIndentedString(currentInstructor)).append("\n");
    sb.append("    batchTypeId: ").append(toIndentedString(batchTypeId)).append("\n");
    sb.append("    batchTypeName: ").append(toIndentedString(batchTypeName)).append("\n");
    sb.append("    classIds: ").append(toIndentedString(classIds)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

