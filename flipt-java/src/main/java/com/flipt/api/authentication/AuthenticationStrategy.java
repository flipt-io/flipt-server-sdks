package com.flipt.api.authentication;

public interface AuthenticationStrategy {
  String getAuthorizationHeader();
}
