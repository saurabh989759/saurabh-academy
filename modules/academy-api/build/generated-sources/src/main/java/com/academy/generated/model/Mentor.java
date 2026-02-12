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
 * Mentor
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class Mentor {

  private Long id;

  private String name;

  private String currentCompany;

  public Mentor id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Mentor ID
   * @return id
  */
  
  @Schema(name = "id", description = "Mentor ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Mentor name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Mentor full name
   * @return name
  */
  
  @Schema(name = "name", example = "Alice Johnson", description = "Mentor full name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Mentor currentCompany(String currentCompany) {
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
    Mentor mentor = (Mentor) o;
    return Objects.equals(this.id, mentor.id) &&
        Objects.equals(this.name, mentor.name) &&
        Objects.equals(this.currentCompany, mentor.currentCompany);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, currentCompany);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Mentor {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

