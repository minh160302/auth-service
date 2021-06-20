package com.auth.service.exception;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BusinessException extends Exception {

  private String fieldName;

  private ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public BusinessException(String fieldName, ErrorCode errorCode) {
    this.fieldName = fieldName;
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public BusinessException(String fieldName, ErrorCode errorCode, String message) {
    super(message);
    this.fieldName = fieldName;
    this.errorCode = errorCode;
  }

  public BusinessException(String message) {

  }

  public BusinessException(String message, Throwable t) {
    super(message, t);
  }
}
