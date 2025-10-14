using System.Text.Json.Serialization;

namespace Flipt.Models;

/// <summary>
/// Represents the response from listing flags.
/// </summary>
public class ListFlagsResponse : PaginatedResponse
{
    [JsonPropertyName("flags")]
    public List<Flag>? Flags { get; set; }
}
