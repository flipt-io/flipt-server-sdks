namespace Flipt.Authentication;

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

    public KeyValuePair<string, string> GetAuthorizationHeader()
    {
        return new KeyValuePair<string, string>("JWT", _jwtToken);
    }
}