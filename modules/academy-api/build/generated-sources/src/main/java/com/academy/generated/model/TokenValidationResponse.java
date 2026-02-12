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
 * TokenValidationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-02T15:35:10.663298+05:30[Asia/Kolkata]", comments = "Generator version: 7.5.0")
public class TokenValidationResponse {

  private Boolean valid;

  private String username;

  private String error;

  public TokenValidationResponse valid(Boolean valid) {
    this.valid = valid;
    return this;
  }

  /**
   * Whether the token is valid
   * @return valid
  */
  
  @Schema(name = "valid", example = "true", description = "Whether the token is valid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("valid")
  public Boolean isValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  public TokenValidationResponse username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Username extracted from token (if valid)
   * @return username
  */
  
  @Schema(name = "username", example = "admin@academy.com", description = "Username extracted from token (if valid)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public TokenValidationResponse error(String error) {
    this.error = error;
    return this;
  }

  /**
   * Error message (if validation failed)
   * @return error
  */
  
  @Schema(name = "error", description = "Error message (if validation failed)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TokenValidationResponse tokenValidationResponse = (TokenValidationResponse) o;
    return Objects.equals(this.valid, tokenValidationResponse.valid) &&
        Objects.equals(this.username, tokenValidationResponse.username) &&
        Objects.equals(this.error, tokenValidationResponse.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(valid, username, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TokenValidationResponse {\n");
    sb.append("    valid: ").append(toIndentedString(valid)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

