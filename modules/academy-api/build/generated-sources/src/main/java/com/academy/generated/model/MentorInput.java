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
 * MentorInput
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class MentorInput {

  private String name;

  private String currentCompany;

  public MentorInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MentorInput(String name) {
    this.name = name;
  }

  public MentorInput name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Mentor full name
   * @return name
  */
  @NotNull 
  @Schema(name = "name", example = "Alice Johnson", description = "Mentor full name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MentorInput currentCompany(String currentCompany) {
    this.currentCompany = currentCompany;
    return this;
  }

  /**
   * Current company where mentor works
   * @return currentCompany
  */
  
  @Schema(name = "currentCompany", example = "Tech Corp", description = "Current company where mentor works", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currentCompany")
  public String getCurrentCompany() {
    return currentCompany;
  }

  public void setCurrentCompany(String currentCompany) {
    this.currentCompany = currentCompany;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MentorInput mentorInput = (MentorInput) o;
    return Objects.equals(this.name, mentorInput.name) &&
        Objects.equals(this.currentCompany, mentorInput.currentCompany);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, currentCompany);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MentorInput {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    currentCompany: ").append(toIndentedString(currentCompany)).append("\n");
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

