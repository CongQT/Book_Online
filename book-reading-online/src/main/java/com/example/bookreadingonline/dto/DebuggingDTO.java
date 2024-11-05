package com.example.bookreadingonline.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebuggingDTO {

  @JsonProperty("message")
  private String message;

  @JsonProperty("root_cause_message")
  private String rootCauseMessage;

  @JsonProperty("stack_trace")
  private Collection<String> stackTrace;

  public static DebuggingDTO build(Throwable throwable) {
    try {
      if (throwable != null) {
        return DebuggingDTO.builder()
            .message(ExceptionUtils.getMessage(throwable))
            .rootCauseMessage(ExceptionUtils.getRootCauseMessage(throwable))
            .stackTrace(getStackTrace(throwable))
            .build();
      }
    } catch (Exception e) {
      log.error("Error when build DebuggingDTO", e);
    }
    return new DebuggingDTO();
  }

  private static Collection<String> getStackTrace(Throwable throwable) {
    Set<String> stackTrace = new LinkedHashSet<>();
    if (throwable != null) {
      stackTrace.addAll(getStackTrace(throwable.getCause()));
      stackTrace.addAll(Arrays.stream(throwable.getStackTrace())
          .map(stackTraceElement -> String.format("%s.%s(%d)",
              stackTraceElement.getClassName(),
              stackTraceElement.getMethodName(),
              stackTraceElement.getLineNumber()))
          .collect(Collectors.toList()));
    }
    return stackTrace;
  }

}
