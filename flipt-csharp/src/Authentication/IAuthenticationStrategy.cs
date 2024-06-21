namespace FliptCSharp.Authentication;

/// <summary>
/// This interface is responsible for providing the authentication strategy.
/// </summary>
public interface IAuthenticationStrategy
{
    KeyValuePair<string, string> GetAuthorizationHeader();
}