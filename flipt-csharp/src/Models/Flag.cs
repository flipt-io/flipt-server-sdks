using System.Text.Json.Serialization;

namespace Flipt.Models;

/// <summary>
/// Represents a flag with its metadata and variants.
/// </summary>
public class Flag
{
    [JsonPropertyName("key")]
    public string? Key { get; set; }

    [JsonPropertyName("name")]
    public string? Name { get; set; }

    [JsonPropertyName("description")]
    public string? Description { get; set; }

    [JsonPropertyName("enabled")]
    public bool Enabled { get; set; }

    [JsonPropertyName("namespaceKey")]
    public string? NamespaceKey { get; set; }

    [JsonPropertyName("type")]
    public FlagType Type { get; set; }

    [JsonPropertyName("createdAt")]
    public DateTime? CreatedAt { get; set; }

    [JsonPropertyName("updatedAt")]
    public DateTime? UpdatedAt { get; set; }

    [JsonPropertyName("variants")]
    public List<Variant>? Variants { get; set; }
}
