package com.academy.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
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
 * ModelClass
 */

@JsonTypeName("Class")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class ModelClass {

  private Long id;

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate date;

  private String time;

  private String instructor;

  public ModelClass id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Class ID
   * @return id
  */
  
  @Schema(name = "id", description = "Class ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ModelClass name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Class name
   * @return name
  */
  
  @Schema(name = "name", example = "Introduction to Java", description = "Class name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ModelClass date(LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Class date
   * @return date
  */
  @Valid 
  @Schema(name = "date", example = "Fri Mar 15 05:30:00 IST 2024", description = "Class date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("date")
  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public ModelClass time(String time) {
    this.time = time;
    return this;
  }

  /**
   * Class time
   * @return time
  */
  
  @Schema(name = "time", example = "10:00:00", description = "Class time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("time")
  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public ModelClass instructor(String instructor) {
    this.instructor = instructor;
    return this;
  }

  /**
   * Instructor name
   * @return instructor
  */
  
  @Schema(name = "instructor", example = "Jane Smith", description = "Instructor name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("instructor")
  public String getInstructor() {
    return instructor;
  }

  public void setInstructor(String instructor) {
    this.instructor = instructor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelClass propertyClass = (ModelClass) o;
    return Objects.equals(this.id, propertyClass.id) &&
        Objects.equals(this.name, propertyClass.name) &&
        Objects.equals(this.date, propertyClass.date) &&
        Objects.equals(this.time, propertyClass.time) &&
        Objects.equals(this.instructor, propertyClass.instructor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, date, time, instructor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelClass {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    instructor: ").append(toIndentedString(instructor)).append("\n");
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

