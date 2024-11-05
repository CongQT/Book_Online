package com.example.bookreadingonline.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {

  @Email(regexp = ".+@.+\\..+", message = "INVALID_EMAIL")
  @JsonProperty("email")
  private String email;

  @JsonProperty("username")
  private String username;

  @Size(min = 8, message = "MIN_LENGTH_INVALID")
  @NotNull(message = "MISSING_PASSWORD")
  @JsonProperty("password")
  private String password;

  @NotNull(message = "MISSING_ROLE")
  @JsonProperty("role_id")
  private Integer roleId;

  @NotBlank(message = "MISSING_NAME")
  private String name;

}