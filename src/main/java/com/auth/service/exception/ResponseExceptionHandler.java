package com.auth.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public final ResponseEntity<ErrorResponseDetail> handleAgencyException(BusinessException ex, WebRequest request){
    ErrorResponseDetail errorDetails = new ErrorResponseDetail(new Date(), ex.getMessage(),
            request.getDescription(false), ex);
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
}
