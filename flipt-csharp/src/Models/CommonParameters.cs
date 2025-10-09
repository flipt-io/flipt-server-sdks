using System.Text.Json.Serialization;

namespace Flipt.Models;

/// <summary>
/// Common parameters for API requests.
/// </summary>
public class CommonParameters
{
    [JsonPropertyName("reference")]
    public string? Reference { get; set; }
}
