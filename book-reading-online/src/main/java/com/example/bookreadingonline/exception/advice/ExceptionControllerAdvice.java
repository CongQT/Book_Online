package com.example.bookreadingonline.exception.advice;

import com.example.bookreadingonline.dto.DebuggingDTO;
import com.example.bookreadingonline.exception.handler.DefaultExceptionHandler;
import com.example.bookreadingonline.exception.handler.ServletExceptionHandler;
import com.example.bookreadingonline.util.MyComparatorUtils;
import com.example.bookreadingonline.util.MyListUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import java.util.Comparator;
import java.util.List;

@Log4j2
@RestController
@RestControllerAdvice
public class ExceptionControllerAdvice extends AbstractErrorController {

  private final ErrorAttributes errorAttributes;
  private final ServletExceptionHandler<Exception> defaultExceptionHandler = new DefaultExceptionHandler();
  private final List<ServletExceptionHandler<? extends Exception>> sortedServletExceptionHandlers;

  public ExceptionControllerAdvice(
      ErrorAttributes errorAttributes,
      List<ServletExceptionHandler<? extends Exception>> servletExceptionHandlers
  ) {
    super(errorAttributes);
    this.errorAttributes = errorAttributes;
    Comparator<ServletExceptionHandler<? extends Exception>> comparator =
        MyComparatorUtils.compareExceptionClass(ServletExceptionHandler::getExceptionClass);
    sortedServletExceptionHandlers = MyListUtils.sorted(servletExceptionHandlers,
        comparator.reversed());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception e, HttpServletRequest request) {

    log.error("Caught exception {} on request {} with debug info {}",
        e.getClass().getSimpleName(), request.getRequestURI(), DebuggingDTO.build(e));

    return sortedServletExceptionHandlers.stream()
        .filter(handler -> handler.getExceptionClass().isInstance(e))
        .findFirst()
        .orElse(defaultExceptionHandler)
        .handleException(e, request);

  }

  @RequestMapping("${error.path:/error}")
  public ResponseEntity<Object> error(HttpServletRequest request) {
    WebRequest webRequest = new ServletWebRequest(request);
    Exception e = (Exception) errorAttributes.getError(webRequest);
    return handleException(e, request);
  }

}
