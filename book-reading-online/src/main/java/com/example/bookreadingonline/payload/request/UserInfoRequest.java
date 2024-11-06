package com.example.bookreadingonline.payload.request;

import com.example.bookreadingonline.payload.response.RoleResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoRequest {

  @JsonProperty("name")
  private String name;

  @JsonProperty("email")
  private String email;

  @JsonProperty("birthday")
  private LocalDate birthday;

  @JsonProperty("gender")
  private String gender;

  @JsonProperty("phone")
  private String phone;

  @JsonProperty("avatar")
  private String avatar;

}