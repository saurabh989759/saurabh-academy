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
 * BatchInput
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class BatchInput {

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate startMonth;

  private String currentInstructor;

  private Long batchTypeId;

  @Valid
  private List<Long> classIds = new ArrayList<>();

  public BatchInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BatchInput(String name, LocalDate startMonth, String currentInstructor, Long batchTypeId) {
    this.name = name;
    this.startMonth = startMonth;
    this.currentInstructor = currentInstructor;
    this.batchTypeId = batchTypeId;
  }

  public BatchInput name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Batch name
   * @return name
  */
  @NotNull 
  @Schema(name = "name", example = "FSD-2024-03", description = "Batch name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BatchInput startMonth(LocalDate startMonth) {
    this.startMonth = startMonth;
    return this;
  }

  /**
   * Start month of the batch
   * @return startMonth
  */
  @NotNull @Valid 
  @Schema(name = "startMonth", example = "Fri Mar 01 05:30:00 IST 2024", description = "Start month of the batch", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("startMonth")
  public LocalDate getStartMonth() {
    return startMonth;
  }

  public void setStartMonth(LocalDate startMonth) {
    this.startMonth = startMonth;
  }

  public BatchInput currentInstructor(String currentInstructor) {
    this.currentInstructor = currentInstructor;
    return this;
  }

  /**
   * Current instructor name
   * @return currentInstructor
  */
  @NotNull 
  @Schema(name = "currentInstructor", example = "John Instructor", description = "Current instructor name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currentInstructor")
  public String getCurrentInstructor() {
    return currentInstructor;
  }

  public void setCurrentInstructor(String currentInstructor) {
    this.currentInstructor = currentInstructor;
  }

  public BatchInput batchTypeId(Long batchTypeId) {
    this.batchTypeId = batchTypeId;
    return this;
  }

  /**
   * Batch type ID
   * @return batchTypeId
  */
  @NotNull 
  @Schema(name = "batchTypeId", description = "Batch type ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("batchTypeId")
  public Long getBatchTypeId() {
    return batchTypeId;
  }

  public void setBatchTypeId(Long batchTypeId) {
    this.batchTypeId = batchTypeId;
  }

  public BatchInput classIds(List<Long> classIds) {
    this.classIds = classIds;
    return this;
  }

  public BatchInput addClassIdsItem(Long classIdsItem) {
    if (this.classIds == null) {
      this.classIds = new ArrayList<>();
    }
    this.classIds.add(classIdsItem);
    return this;
  }

  /**
   * List of class IDs to assign to this batch
   * @return classIds
  */
  
  @Schema(name = "classIds", description = "List of class IDs to assign to this batch", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    BatchInput batchInput = (BatchInput) o;
    return Objects.equals(this.name, batchInput.name) &&
        Objects.equals(this.startMonth, batchInput.startMonth) &&
        Objects.equals(this.currentInstructor, batchInput.currentInstructor) &&
        Objects.equals(this.batchTypeId, batchInput.batchTypeId) &&
        Objects.equals(this.classIds, batchInput.classIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, startMonth, currentInstructor, batchTypeId, classIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BatchInput {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    startMonth: ").append(toIndentedString(startMonth)).append("\n");
    sb.append("    currentInstructor: ").append(toIndentedString(currentInstructor)).append("\n");
    sb.append("    batchTypeId: ").append(toIndentedString(batchTypeId)).append("\n");
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

