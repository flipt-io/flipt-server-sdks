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
    /// Lists flags for the specified namespace key.
    /// </summary>
    /// <param name="namespaceKey">The key of the namespace for which to list flags.</param>
    /// <returns>A task that represents the asynchronous operation. The task result contains a <see cref="ListFlagsResponse"/> if successful; otherwise, <c>null</c>.</returns>
    public async Task<ListFlagsResponse?> ListFlagsAsync(string namespaceKey)
    {
        return await ListFlagsAsync(namespaceKey, null);
    }

    /// <summary>
    /// Lists flags for the specified namespace, optionally filtering with parameters.
    /// </summary>
    /// <param name="namespaceKey">The key of the namespace to list flags from. If null or empty, the default namespace is used.</param>
    /// <param name="parameters">Optional parameters to filter or paginate the flag list.</param>
    /// <returns>A <see cref="ListFlagsResponse"/> containing the list of flags, or null if the request fails.</returns>
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
    /// Makes an asynchronous GET request to the Flipt server and deserializes the response to the specified type.
    /// </summary>
    /// <typeparam name="T">The type to which the response will be deserialized.</typeparam>
    /// <param name="path">The relative path of the API endpoint.</param>
    /// <param name="parameters">An object containing query parameters to be appended to the request URL. Can be null.</param>
    /// <returns>
    /// A task representing the asynchronous operation, with a result of type <typeparamref name="T"/> if the response is not empty; otherwise, <c>null</c>.
    /// </returns>
    /// <exception cref="InvalidOperationException">Thrown if the Flipt base URL is not set.</exception>
    /// <exception cref="HttpRequestException">Thrown when the HTTP request fails.</exception>
    /// <exception cref="JsonException">Thrown when deserialization of the response fails.</exception>
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
    /// Builds a query string from the provided object by serializing its properties as key-value pairs.
    /// </summary>
    /// <param name="parameters">The object whose properties will be converted into query string parameters.</param>
    /// <returns>A string representing the query string constructed from the object's properties.</returns>
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
