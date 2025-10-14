using System.Text.Json.Serialization;

namespace Flipt.Models;

/// <summary>
/// Base class for paginated responses.
/// </summary>
public class PaginatedResponse
{
    [JsonPropertyName("nextPageToken")]
    public string? NextPageToken { get; set; }

    [JsonPropertyName("totalCount")]
    public int TotalCount { get; set; }
}
