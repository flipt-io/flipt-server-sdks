package com.flipt.api.authentication;

public final class ClientTokenAuthenticationStrategy implements AuthenticationStrategy {
  private final String clientToken;

  public ClientTokenAuthenticationStrategy(String clientToken) {
    this.clientToken = clientToken;
  }

  @Override
  public String getAuthorizationHeader() {
    return String.format("Bearer %s", this.clientToken);
  }
}
