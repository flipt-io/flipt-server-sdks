namespace FliptCSharp.Authentication;

/// <summary>
/// This class is responsible for providing the client token authentication strategy.
/// </summary>
public class ClientTokenAuthenticationStrategy : IAuthenticationStrategy
{
    private readonly string _clientToken;

    public ClientTokenAuthenticationStrategy(string clientToken)
    {
        _clientToken = clientToken;
    }
    public string GetAuthorizationHeader()
    {
        return $"Bearer {_clientToken}";
    }
}