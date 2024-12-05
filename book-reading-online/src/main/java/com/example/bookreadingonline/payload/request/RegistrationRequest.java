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


  @NotBlank(message = "MISSING_EMAIL")
  @JsonProperty("email")
  private String email;

  @NotBlank(message = "MISSING_USERNAME")
  @JsonProperty("username")
  private String username;

  @NotBlank(message = "MISSING_PASSWORD")
  @JsonProperty("password")
  private String password;

}