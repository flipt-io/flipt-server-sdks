package io.flipt.api.authentication;

public final class JWTAuthenticationStrategy implements AuthenticationStrategy {
  private final String jwtToken;

  public JWTAuthenticationStrategy(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  @Override
  public String getAuthorizationHeader() {
    return String.format("JWT %s", this.jwtToken);
  }
}
