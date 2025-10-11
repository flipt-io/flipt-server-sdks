using System.Text.Json.Serialization;

namespace Flipt.Models;

/// <summary>
/// Parameters for list API requests with pagination.
/// </summary>
public class ListParameters
{
    [JsonPropertyName("limit")]
    public int? Limit { get; set; }

    [JsonPropertyName("offset")]
    public int? Offset { get; set; }

    [JsonPropertyName("pageToken")]
    public string? PageToken { get; set; }

    [JsonPropertyName("reference")]
    public string? Reference { get; set; }
}
