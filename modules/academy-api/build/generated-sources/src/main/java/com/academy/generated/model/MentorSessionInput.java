package com.academy.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
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
 * MentorSessionInput
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class MentorSessionInput {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime time;

  private Integer durationMinutes;

  private Long studentId;

  private Long mentorId;

  private Integer studentRating;

  private Integer mentorRating;

  public MentorSessionInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MentorSessionInput(OffsetDateTime time, Integer durationMinutes, Long studentId, Long mentorId) {
    this.time = time;
    this.durationMinutes = durationMinutes;
    this.studentId = studentId;
    this.mentorId = mentorId;
  }

  public MentorSessionInput time(OffsetDateTime time) {
    this.time = time;
    return this;
  }

  /**
   * Session scheduled time
   * @return time
  */
  @NotNull @Valid 
  @Schema(name = "time", example = "2024-03-20T15:00Z", description = "Session scheduled time", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("time")
  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime(OffsetDateTime time) {
    this.time = time;
  }

  public MentorSessionInput durationMinutes(Integer durationMinutes) {
    this.durationMinutes = durationMinutes;
    return this;
  }

  /**
   * Session duration in minutes
   * @return durationMinutes
  */
  @NotNull 
  @Schema(name = "durationMinutes", example = "60", description = "Session duration in minutes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("durationMinutes")
  public Integer getDurationMinutes() {
    return durationMinutes;
  }

  public void setDurationMinutes(Integer durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public MentorSessionInput studentId(Long studentId) {
    this.studentId = studentId;
    return this;
  }

  /**
   * Student ID
   * @return studentId
  */
  @NotNull 
  @Schema(name = "studentId", description = "Student ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("studentId")
  public Long getStudentId() {
    return studentId;
  }

  public void setStudentId(Long studentId) {
    this.studentId = studentId;
  }

  public MentorSessionInput mentorId(Long mentorId) {
    this.mentorId = mentorId;
    return this;
  }

  /**
   * Mentor ID
   * @return mentorId
  */
  @NotNull 
  @Schema(name = "mentorId", description = "Mentor ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mentorId")
  public Long getMentorId() {
    return mentorId;
  }

  public void setMentorId(Long mentorId) {
    this.mentorId = mentorId;
  }

  public MentorSessionInput studentRating(Integer studentRating) {
    this.studentRating = studentRating;
    return this;
  }

  /**
   * Student's rating of the session (1-5)
   * minimum: 1
   * maximum: 5
   * @return studentRating
  */
  @Min(1) @Max(5) 
  @Schema(name = "studentRating", description = "Student's rating of the session (1-5)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("studentRating")
  public Integer getStudentRating() {
    return studentRating;
  }

  public void setStudentRating(Integer studentRating) {
    this.studentRating = studentRating;
  }

  public MentorSessionInput mentorRating(Integer mentorRating) {
    this.mentorRating = mentorRating;
    return this;
  }

  /**
   * Mentor's rating of the session (1-5)
   * minimum: 1
   * maximum: 5
   * @return mentorRating
  */
  @Min(1) @Max(5) 
  @Schema(name = "mentorRating", description = "Mentor's rating of the session (1-5)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mentorRating")
  public Integer getMentorRating() {
    return mentorRating;
  }

  public void setMentorRating(Integer mentorRating) {
    this.mentorRating = mentorRating;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MentorSessionInput mentorSessionInput = (MentorSessionInput) o;
    return Objects.equals(this.time, mentorSessionInput.time) &&
        Objects.equals(this.durationMinutes, mentorSessionInput.durationMinutes) &&
        Objects.equals(this.studentId, mentorSessionInput.studentId) &&
        Objects.equals(this.mentorId, mentorSessionInput.mentorId) &&
        Objects.equals(this.studentRating, mentorSessionInput.studentRating) &&
        Objects.equals(this.mentorRating, mentorSessionInput.mentorRating);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, durationMinutes, studentId, mentorId, studentRating, mentorRating);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MentorSessionInput {\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    durationMinutes: ").append(toIndentedString(durationMinutes)).append("\n");
    sb.append("    studentId: ").append(toIndentedString(studentId)).append("\n");
    sb.append("    mentorId: ").append(toIndentedString(mentorId)).append("\n");
    sb.append("    studentRating: ").append(toIndentedString(studentRating)).append("\n");
    sb.append("    mentorRating: ").append(toIndentedString(mentorRating)).append("\n");
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

