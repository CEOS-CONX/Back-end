package com.conx.server.global.exception;

public class CustomAuthenticationException extends RuntimeException {
  public CustomAuthenticationException(String message) {
    super(message);
  }
}
