package io.flipt.api.authentication;

public interface AuthenticationStrategy {
  String getAuthorizationHeader();
}
