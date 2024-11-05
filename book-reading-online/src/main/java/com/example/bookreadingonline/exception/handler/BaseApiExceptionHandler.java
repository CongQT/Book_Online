package com.example.bookreadingonline.exception.handler;

import com.example.bookreadingonline.exception.BaseApiException;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Log4j2
@Component
public class BaseApiExceptionHandler implements ServletExceptionHandler<BaseApiException> {

    @Override
    public ResponseEntity<Object> handle(BaseApiException e, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.create()
                .errorCodes(e.errorCodes())
                .extraData(e.extraData())
                .buildDebugInfo(e);

        return ResponseEntity.status(getStatus(e))
                .body(BaseResponse.of(errorResponse));
    }

    private HttpStatus getStatus(BaseApiException e) {
        if (e.getClass().isAnnotationPresent(ResponseStatus.class)) {
            return e.getClass().getAnnotation(ResponseStatus.class).value();
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
