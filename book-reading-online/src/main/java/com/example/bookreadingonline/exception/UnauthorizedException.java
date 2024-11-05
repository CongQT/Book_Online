package com.example.bookreadingonline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BaseApiException {

  public UnauthorizedException(String msg) {
    super(msg);
  }

}
