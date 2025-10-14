using System.Text.Json.Serialization;

namespace Flipt.Models;

/// <summary>
/// Represents a variant of a flag.
/// </summary>
public class Variant
{
    [JsonPropertyName("attachment")]
    public string? Attachment { get; set; }

    [JsonPropertyName("description")]
    public string? Description { get; set; }

    [JsonPropertyName("flagKey")]
    public string? FlagKey { get; set; }

    [JsonPropertyName("id")]
    public string? Id { get; set; }

    [JsonPropertyName("key")]
    public string? Key { get; set; }

    [JsonPropertyName("name")]
    public string? Name { get; set; }

    [JsonPropertyName("namespaceKey")]
    public string? NamespaceKey { get; set; }

    [JsonPropertyName("createdAt")]
    public DateTime? CreatedAt { get; set; }

    [JsonPropertyName("updatedAt")]
    public DateTime? UpdatedAt { get; set; }
}
