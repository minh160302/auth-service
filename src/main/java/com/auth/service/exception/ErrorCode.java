package com.auth.service.exception;

public enum ErrorCode {
  BAD_REQUEST(400, "bad request"),
  NULL_OR_EMPTY(404, "null or empty"),

  ;

  private ErrorCode(int statusCode, String description) {
    this.statusCode = statusCode;
    this.description = description;
  }

  private final int statusCode;
  private final String description;

  public int getStatusCode() {
    return statusCode;
  }

  public String getDescription() {
    return description;
  }
}
