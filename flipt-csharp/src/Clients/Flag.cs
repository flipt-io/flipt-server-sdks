using System.Net.Http.Headers;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Web;
using Flipt.Authentication;
using Flipt.Models;

namespace Flipt.Clients;

/// <summary>
/// This class is responsible for making requests to the Flipt server to manage flags.
/// </summary>
public class Flag
{
    private const string DefaultNamespaceKey = "default";
    private readonly HttpClient _httpClient;
    private readonly string? _baseUrl;
    private readonly IAuthenticationStrategy? _authenticationStrategy;
    private readonly IDictionary<string, string>? _headers;

    private readonly JsonSerializerOptions jsonSerializeSettings = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
        Converters =
        {
            new JsonStringEnumConverter(JsonNamingPolicy.SnakeCaseUpper)
        }
    };

    /// <summary>
    /// This method creates a new instance of the Flag class.
    /// </summary>
    /// <param name="builder"></param>
    private Flag(FlagBuilder builder)
    {
        _httpClient = builder.HttpClient;
        _baseUrl = builder.BaseUrl;
        _authenticationStrategy = builder.AuthenticationStrategy;
        _headers = builder.Headers;
    }

    /// <summary>
    /// This method returns a new instance of the FlagBuilder class.
    /// </summary>
    /// <returns></returns>
    public static FlagBuilder Builder()
    {
        return new FlagBuilder();
    }

    /// <summary>
    /// This class is a builder for the Flag class.
    /// </summary>
    public class FlagBuilder
    {
        public HttpClient HttpClient { get; private set; } = null!;
        public string? BaseUrl { get; private set; } = null!;
        public IAuthenticationStrategy? AuthenticationStrategy { get; private set; }
        public IDictionary<string, string>? Headers { get; private set; }

        public FlagBuilder WithHttpClient(HttpClient httpClient)
        {
            HttpClient = httpClient;
            return this;
        }

        public FlagBuilder WithBaseUrl(string? baseUrl)
        {
            BaseUrl = baseUrl;
            return this;
        }

        public FlagBuilder WithAuthenticationStrategy(IAuthenticationStrategy? authenticationStrategy)
        {
            AuthenticationStrategy = authenticationStrategy;
            return this;
        }

        public FlagBuilder WithHeaders(IDictionary<string, string>? headers)
        {
            Headers = headers;
            return this;
        }

        public Flag Build()
        {
            if (HttpClient == null)
            {
                throw new InvalidOperationException("HttpClient must be provided.");
            }

            if (string.IsNullOrEmpty(BaseUrl))
            {
                throw new InvalidOperationException("BaseURL must be provided.");
            }

            return new Flag(this);
        }
    }

    /// <summary>
    /// This method gets a flag by namespace key and flag key.
    /// </summary>
    /// <param name="namespaceKey"></param>
    /// <param name="flagKey"></param>
    /// <returns></returns>
    public async Task<Models.Flag?> GetFlagAsync(string namespaceKey, string flagKey)
    {
        return await GetFlagAsync(namespaceKey, flagKey, null);
    }

    /// <summary>
    /// This method gets a flag by namespace key and flag key with parameters.
    /// </summary>
    /// <param name="namespaceKey"></param>
    /// <param name="flagKey"></param>
    /// <param name="parameters"></param>
    /// <returns></returns>
    public async Task<Models.Flag?> GetFlagAsync(string namespaceKey, string flagKey, CommonParameters? parameters)
    {
        if (string.IsNullOrEmpty(namespaceKey))
        {
            namespaceKey = DefaultNamespaceKey;
        }

        var path = $"/api/v1/namespaces/{namespaceKey}/flags/{flagKey}";
        return await MakeGetRequestAsync<Models.Flag>(path, parameters);
    }

    /// <summary>
    /// This method lists flags by namespace key.
    /// </summary>
    /// <param name="namespaceKey"></param>
    /// <returns></returns>
    public async Task<ListFlagsResponse?> ListFlagsAsync(string namespaceKey)
    {
        return await ListFlagsAsync(namespaceKey, null);
    }

    /// <summary>
    /// This method lists flags by namespace key with parameters.
    /// </summary>
    /// <param name="namespaceKey"></param>
    /// <param name="parameters"></param>
    /// <returns></returns>
    public async Task<ListFlagsResponse?> ListFlagsAsync(string namespaceKey, ListParameters? parameters)
    {
        if (string.IsNullOrEmpty(namespaceKey))
        {
            namespaceKey = DefaultNamespaceKey;
        }

        var path = $"/api/v1/namespaces/{namespaceKey}/flags";
        return await MakeGetRequestAsync<ListFlagsResponse>(path, parameters);
    }

    /// <summary>
    /// This method makes a GET request to the Flipt server.
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="path"></param>
    /// <param name="parameters"></param>
    /// <returns></returns>
    private async Task<T?> MakeGetRequestAsync<T>(string path, object? parameters)
    {
        if (_baseUrl == null)
        {
            throw new InvalidOperationException("Flipt Url is not set");
        }

        var url = _baseUrl.TrimEnd('/') + path;

        // Add query parameters if provided
        if (parameters != null)
        {
            var queryParams = BuildQueryString(parameters);
            if (!string.IsNullOrEmpty(queryParams))
            {
                url += "?" + queryParams;
            }
        }

        var httpRequest = new HttpRequestMessage(HttpMethod.Get, url);

        if (_headers != null)
        {
            foreach (var header in _headers)
            {
                httpRequest.Headers.Add(header.Key, header.Value);
            }
        }

        if (_authenticationStrategy != null)
        {
            var authorizationHeader = _authenticationStrategy.GetAuthorizationHeader();
            httpRequest.Headers.Authorization = new AuthenticationHeaderValue(authorizationHeader.Key, authorizationHeader.Value);
        }

        var response = await _httpClient.SendAsync(httpRequest);
        response.EnsureSuccessStatusCode();

        var responseContent = await response.Content.ReadAsStringAsync();
        return string.IsNullOrEmpty(responseContent) ? default : JsonSerializer.Deserialize<T>(responseContent, jsonSerializeSettings);
    }

    /// <summary>
    /// This method builds a query string from an object.
    /// </summary>
    /// <param name="parameters"></param>
    /// <returns></returns>
    private string BuildQueryString(object parameters)
    {
        var jsonString = JsonSerializer.Serialize(parameters, jsonSerializeSettings);
        var dictionary = JsonSerializer.Deserialize<Dictionary<string, JsonElement>>(jsonString);

        if (dictionary == null)
        {
            return string.Empty;
        }

        var queryParams = new List<string>();
        foreach (var kvp in dictionary)
        {
            if (kvp.Value.ValueKind != JsonValueKind.Null && kvp.Value.ValueKind != JsonValueKind.Undefined)
            {
                var value = kvp.Value.ValueKind == JsonValueKind.String
                    ? kvp.Value.GetString()
                    : kvp.Value.ToString();

                if (!string.IsNullOrEmpty(value))
                {
                    queryParams.Add($"{HttpUtility.UrlEncode(kvp.Key)}={HttpUtility.UrlEncode(value)}");
                }
            }
        }

        return string.Join("&", queryParams);
    }
}
