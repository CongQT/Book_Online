package com.example.bookreadingonline.payload.response.base;

import com.example.bookreadingonline.dto.DebuggingDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor(staticName = "create")
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

  @JsonProperty("error_codes")
  private List<String> errorCodes;

  @JsonProperty("extra_data")
  private Object extraData;

  @JsonProperty("debug_info")
  private DebuggingDTO debugInfo;

  public ErrorResponse errorCodes(String... errorCodes) {
    return errorCodes(Arrays.asList(errorCodes));
  }

  public ErrorResponse errorCodes(Collection<String> errorCodes) {
    this.errorCodes = new ArrayList<>(CollectionUtils.emptyIfNull(errorCodes));
    return this;
  }

  public ErrorResponse extraData(Object extraData) {
    this.extraData = extraData;
    return this;
  }

  public ErrorResponse buildDebugInfo(Exception e) {
    this.debugInfo = DebuggingDTO.build(e);
    return this;
  }
}
