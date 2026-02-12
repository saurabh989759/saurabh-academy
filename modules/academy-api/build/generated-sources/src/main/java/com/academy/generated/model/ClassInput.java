package com.academy.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * ClassInput
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class ClassInput {

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate date;

  private String time;

  private String instructor;

  public ClassInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ClassInput(String name, LocalDate date, String time, String instructor) {
    this.name = name;
    this.date = date;
    this.time = time;
    this.instructor = instructor;
  }

  public ClassInput name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Class name
   * @return name
  */
  @NotNull 
  @Schema(name = "name", example = "Introduction to Java", description = "Class name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ClassInput date(LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Class date
   * @return date
  */
  @NotNull @Valid 
  @Schema(name = "date", example = "Fri Mar 15 05:30:00 IST 2024", description = "Class date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("date")
  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public ClassInput time(String time) {
    this.time = time;
    return this;
  }

  /**
   * Class time
   * @return time
  */
  @NotNull 
  @Schema(name = "time", example = "10:00:00", description = "Class time", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("time")
  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public ClassInput instructor(String instructor) {
    this.instructor = instructor;
    return this;
  }

  /**
   * Instructor name
   * @return instructor
  */
  @NotNull 
  @Schema(name = "instructor", example = "Jane Smith", description = "Instructor name", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ClassInput classInput = (ClassInput) o;
    return Objects.equals(this.name, classInput.name) &&
        Objects.equals(this.date, classInput.date) &&
        Objects.equals(this.time, classInput.time) &&
        Objects.equals(this.instructor, classInput.instructor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, date, time, instructor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClassInput {\n");
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

