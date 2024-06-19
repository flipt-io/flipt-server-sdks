namespace FliptCSharp.Authentication;

/// <summary>
/// This class is responsible for providing the JWT authentication strategy.
/// </summary>
public class JWTAuthenticationStrategy : IAuthenticationStrategy
{
    private readonly string _jwtToken;

    public JWTAuthenticationStrategy(string jwtToken)
    {
        _jwtToken = jwtToken;
    }

    public string GetAuthorizationHeader()
    {
        return $"JWT {_jwtToken}";
    }
}