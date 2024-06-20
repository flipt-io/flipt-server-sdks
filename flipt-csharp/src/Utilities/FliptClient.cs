using FliptCSharp.Authentication;
using FliptCSharp.Clients;

namespace FliptCSharp.Utilities;

/// <summary>
/// This class is a wrapper around the Evaluation class. It provides a builder pattern to create an instance of the Evaluation class.
/// </summary>
public class FliptClient
{
    /// <summary>
    /// This method returns a new instance of the FliptClientBuilder class.
    /// </summary>
    /// <param name="builder"></param>
    private FliptClient(FliptClientBuilder builder)
    {

        var httpClient = new HttpClient()
        {
            Timeout = builder.Timeout
        };
            
        Evaluation = Evaluation.Builder()
            .WithHttpClient(httpClient)
            .WithBaseUrl(builder.BaseUrl)
            .WithAuthenticationStrategy(builder.AuthenticationStrategy)
            .WithHeaders(builder.Headers)
            .Build();
    }

    public Evaluation Evaluation { get; }

    /// <summary>
    /// This method returns a new instance of the FliptClientBuilder class.
    /// </summary>
    /// <returns></returns>
    public static FliptClientBuilder Builder()
    {
        return new FliptClientBuilder();
    }

    /// <summary>
    /// This class is a builder for the FliptClient class.
    /// </summary>
    public class FliptClientBuilder
    {
        public string BaseUrl { get; private set; } = "http://localhost:8080";
        public IAuthenticationStrategy? AuthenticationStrategy { get; private set; }
        public IDictionary<string, string>? Headers { get; private set; } = new Dictionary<string, string>();
        public TimeSpan Timeout { get; private set; } = TimeSpan.FromSeconds(60);

        /// <summary>
        /// This method sets the base URL for the FliptClient.
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public FliptClientBuilder WithUrl(string url)
        {
            BaseUrl = url;
            return this;
        }

        /// <summary>
        /// This method sets the authentication strategy for the FliptClient.
        /// </summary>
        /// <param name="authenticationStrategy"></param>
        /// <returns></returns>
        public FliptClientBuilder WithAuthentication(IAuthenticationStrategy authenticationStrategy)
        {
            AuthenticationStrategy = authenticationStrategy;
            return this;
        }

        /// <summary>
        /// This method sets the headers for the FliptClient.
        /// </summary>
        /// <param name="headers"></param>
        /// <returns></returns>
        public FliptClientBuilder WithHeaders(IDictionary<string, string> headers)
        {
            Headers = headers;
            return this;
        }

        /// <summary>
        /// This method sets the timeout for the FliptClient.
        /// </summary>
        /// <param name="timeout"></param>
        /// <returns></returns>
        public FliptClientBuilder WithTimeout(TimeSpan timeout)
        {
            Timeout = timeout;
            return this;
        }

        /// <summary>
        /// This method sets the timeout in seconds for the FliptClient.
        /// </summary>
        /// <param name="timeoutInSeconds"></param>
        /// <returns></returns>
        public FliptClientBuilder WithTimeout(int timeoutInSeconds)
        {
            Timeout = TimeSpan.FromSeconds(timeoutInSeconds);
            return this;
        }

        /// <summary>
        /// This method builds a new instance of the FliptClient class.
        /// </summary>
        /// <returns></returns>
        public FliptClient Build()
        {
            return new FliptClient(this);
        }
    }
}