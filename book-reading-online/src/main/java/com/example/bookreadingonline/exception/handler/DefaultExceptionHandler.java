package com.example.bookreadingonline.exception.handler;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
@Log4j2
@Component
public class DefaultExceptionHandler implements ServletExceptionHandler<Exception> {

  @Override
  public ResponseEntity<Object> handle(Exception e, HttpServletRequest request) {

    log.error("Handle undefined exception: ", e);

    ErrorResponse errorResponse = ErrorResponse.create()
        .errorCodes(ErrorCode.UNKNOWN)
        .buildDebugInfo(e);

    return ResponseEntity.status(getStatus(request))
        .body(BaseResponse.of(errorResponse));

  }

  private HttpStatus getStatus(HttpServletRequest request) {

    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    } else {
      try {
        return HttpStatus.valueOf(statusCode);
      } catch (Exception e) {
        return HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }

  }

}
