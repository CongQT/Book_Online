package com.example.bookreadingonline.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoResponse {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("username")
  private String username;

  @JsonProperty("role")
  private RoleResponse role;

  @JsonProperty("name")
  private String name;

  @JsonProperty("email")
  private String email;

}