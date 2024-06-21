namespace Flipt.Authentication;

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
    public KeyValuePair<string, string> GetAuthorizationHeader()
    {
        return new KeyValuePair<string, string>("Bearer", _clientToken);
    }
}