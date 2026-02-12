package com.academy.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Student
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class Student {

  private Long id;

  private String name;

  private String email;

  private Integer graduationYear;

  private String universityName;

  private String phoneNumber;

  private Long batchId;

  private Long buddyId;

  public Student id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Student ID
   * @return id
  */
  
  @Schema(name = "id", description = "Student ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Student full name
   * @return name
  */
  
  @Schema(name = "name", example = "John Doe", description = "Student full name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Student email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Student email address
   * @return email
  */
  @jakarta.validation.constraints.Email 
  @Schema(name = "email", example = "john.doe@example.com", description = "Student email address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Student graduationYear(Integer graduationYear) {
    this.graduationYear = graduationYear;
    return this;
  }

  /**
   * Year of graduation
   * @return graduationYear
  */
  
  @Schema(name = "graduationYear", example = "2024", description = "Year of graduation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("graduationYear")
  public Integer getGraduationYear() {
    return graduationYear;
  }

  public void setGraduationYear(Integer graduationYear) {
    this.graduationYear = graduationYear;
  }

  public Student universityName(String universityName) {
    this.universityName = universityName;
    return this;
  }

  /**
   * Name of the university
   * @return universityName
  */
  
  @Schema(name = "universityName", example = "State University", description = "Name of the university", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("universityName")
  public String getUniversityName() {
    return universityName;
  }

  public void setUniversityName(String universityName) {
    this.universityName = universityName;
  }

  public Student phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Contact phone number
   * @return phoneNumber
  */
  
  @Schema(name = "phoneNumber", example = "+1-555-123-4567", description = "Contact phone number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Student batchId(Long batchId) {
    this.batchId = batchId;
    return this;
  }

  /**
   * ID of the batch the student belongs to
   * @return batchId
  */
  
  @Schema(name = "batchId", description = "ID of the batch the student belongs to", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("batchId")
  public Long getBatchId() {
    return batchId;
  }

  public void setBatchId(Long batchId) {
    this.batchId = batchId;
  }

  public Student buddyId(Long buddyId) {
    this.buddyId = buddyId;
    return this;
  }

  /**
   * ID of the student's buddy
   * @return buddyId
  */
  
  @Schema(name = "buddyId", description = "ID of the student's buddy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("buddyId")
  public Long getBuddyId() {
    return buddyId;
  }

  public void setBuddyId(Long buddyId) {
    this.buddyId = buddyId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Student student = (Student) o;
    return Objects.equals(this.id, student.id) &&
        Objects.equals(this.name, student.name) &&
        Objects.equals(this.email, student.email) &&
        Objects.equals(this.graduationYear, student.graduationYear) &&
        Objects.equals(this.universityName, student.universityName) &&
        Objects.equals(this.phoneNumber, student.phoneNumber) &&
        Objects.equals(this.batchId, student.batchId) &&
        Objects.equals(this.buddyId, student.buddyId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email, graduationYear, universityName, phoneNumber, batchId, buddyId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Student {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    graduationYear: ").append(toIndentedString(graduationYear)).append("\n");
    sb.append("    universityName: ").append(toIndentedString(universityName)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    batchId: ").append(toIndentedString(batchId)).append("\n");
    sb.append("    buddyId: ").append(toIndentedString(buddyId)).append("\n");
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

