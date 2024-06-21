using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using FliptCSharp.Authentication;
using FliptCSharp.DTOs;

namespace FliptCSharp.Clients;
/// <summary>
/// This class is responsible for making requests to the Flipt server to evaluate flags.
/// </summary>
public class Evaluation
{
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
    /// This method creates a new instance of the Evaluation class.
    /// </summary>
    /// <param name="builder"></param>
    private Evaluation(EvaluationBuilder builder)
    {
        _httpClient = builder.HttpClient;
        _baseUrl = builder.BaseUrl;
        _authenticationStrategy = builder.AuthenticationStrategy;
        _headers = builder.Headers;
    }

    /// <summary>
    /// This method returns a new instance of the EvaluationBuilder class.
    /// </summary>
    /// <returns></returns>
    public static EvaluationBuilder Builder()
    {
        return new EvaluationBuilder();
    }

    /// <summary>
    /// This class is a builder for the Evaluation class.
    /// </summary>
    public class EvaluationBuilder
    {
        public HttpClient HttpClient { get; private set; } = null!;
        public string? BaseUrl { get; private set; } = null!;
        public IAuthenticationStrategy? AuthenticationStrategy { get; private set; }
        public IDictionary<string, string>? Headers { get; private set; }

        public EvaluationBuilder WithHttpClient(HttpClient httpClient)
        {
            HttpClient = httpClient;
            return this;
        }

        public EvaluationBuilder WithBaseUrl(string? baseUrl)
        {
            BaseUrl = baseUrl;
            return this;
        }

        public EvaluationBuilder WithAuthenticationStrategy(IAuthenticationStrategy? authenticationStrategy)
        {
            AuthenticationStrategy = authenticationStrategy;
            return this;
        }

        public EvaluationBuilder WithHeaders(IDictionary<string, string>? headers)
        {
            Headers = headers;
            return this;
        }

        public Evaluation? Build()
        {
            if (HttpClient == null)
            {
                throw new InvalidOperationException("HttpClient must be provided.");
            }

            if (string.IsNullOrEmpty(BaseUrl))
            {
                throw new InvalidOperationException("BaseURL must be provided.");
            }

            return new Evaluation(this);
        }
    }

    /// <summary>
    /// This method evaluates a variant for a given flag key and entity id.
    /// </summary>
    /// <param name="request"></param>
    /// <returns></returns>
    public async Task<VariantEvaluationResponse?> EvaluateVariantAsync(EvaluationRequest request)
    {
        return await EvaluateAsync<VariantEvaluationResponse>("/evaluate/v1/variant", request);
    }

    /// <summary>
    /// This method evaluates a boolean flag for a given flag key and entity id.
    /// </summary>
    /// <param name="request"></param>
    /// <returns></returns>
    public async Task<BooleanEvaluationResponse?> EvaluateBooleanAsync(EvaluationRequest request)
    {
        return await EvaluateAsync<BooleanEvaluationResponse>("/evaluate/v1/boolean", request);
    }

    /// <summary>
    /// This method evaluates a batch of flags for a given flag key and entity id.
    /// </summary>
    /// <param name="request"></param>
    /// <returns></returns>
    public async Task<BatchEvaluationResponse?> EvaluateBatchAsync(BatchEvaluationRequest request)
    {
        return await EvaluateAsync<BatchEvaluationResponse>("/evaluate/v1/batch", request);
    }

    /// <summary>
    /// This method evaluates a flag for a given flag key and entity id.
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="path"></param>
    /// <param name="request"></param>
    /// <returns></returns>
    private async Task<T?> EvaluateAsync<T>(string path, object request)
    {
        var url = new Uri(new Uri(_baseUrl ?? throw new InvalidOperationException("Flipt Url is not set")), path);

        var jsonContent = JsonSerializer.Serialize(request, jsonSerializeSettings);
        var content = new StringContent(jsonContent, Encoding.UTF8, "application/json");

        var httpRequest = new HttpRequestMessage(HttpMethod.Post, url)
        {
            Content = content
        };

        if (_headers != null)
            foreach (var header in _headers)
            {
                httpRequest.Headers.Add(header.Key, header.Value);
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

}