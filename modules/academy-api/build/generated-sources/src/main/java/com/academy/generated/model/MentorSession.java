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
 * MentorSession
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class MentorSession {

  private Long id;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime time;

  private Integer durationMinutes;

  private Long studentId;

  private Long mentorId;

  private Integer studentRating;

  private Integer mentorRating;

  public MentorSession id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Mentor session ID
   * @return id
  */
  
  @Schema(name = "id", description = "Mentor session ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public MentorSession time(OffsetDateTime time) {
    this.time = time;
    return this;
  }

  /**
   * Session scheduled time
   * @return time
  */
  @Valid 
  @Schema(name = "time", example = "2024-03-20T15:00Z", description = "Session scheduled time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("time")
  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime(OffsetDateTime time) {
    this.time = time;
  }

  public MentorSession durationMinutes(Integer durationMinutes) {
    this.durationMinutes = durationMinutes;
    return this;
  }

  /**
   * Session duration in minutes
   * @return durationMinutes
  */
  
  @Schema(name = "durationMinutes", example = "60", description = "Session duration in minutes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("durationMinutes")
  public Integer getDurationMinutes() {
    return durationMinutes;
  }

  public void setDurationMinutes(Integer durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public MentorSession studentId(Long studentId) {
    this.studentId = studentId;
    return this;
  }

  /**
   * Student ID
   * @return studentId
  */
  
  @Schema(name = "studentId", description = "Student ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("studentId")
  public Long getStudentId() {
    return studentId;
  }

  public void setStudentId(Long studentId) {
    this.studentId = studentId;
  }

  public MentorSession mentorId(Long mentorId) {
    this.mentorId = mentorId;
    return this;
  }

  /**
   * Mentor ID
   * @return mentorId
  */
  
  @Schema(name = "mentorId", description = "Mentor ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mentorId")
  public Long getMentorId() {
    return mentorId;
  }

  public void setMentorId(Long mentorId) {
    this.mentorId = mentorId;
  }

  public MentorSession studentRating(Integer studentRating) {
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

  public MentorSession mentorRating(Integer mentorRating) {
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
    MentorSession mentorSession = (MentorSession) o;
    return Objects.equals(this.id, mentorSession.id) &&
        Objects.equals(this.time, mentorSession.time) &&
        Objects.equals(this.durationMinutes, mentorSession.durationMinutes) &&
        Objects.equals(this.studentId, mentorSession.studentId) &&
        Objects.equals(this.mentorId, mentorSession.mentorId) &&
        Objects.equals(this.studentRating, mentorSession.studentRating) &&
        Objects.equals(this.mentorRating, mentorSession.mentorRating);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, time, durationMinutes, studentId, mentorId, studentRating, mentorRating);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MentorSession {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

