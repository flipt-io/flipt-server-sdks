using System.ComponentModel.DataAnnotations;

namespace FliptCSharp.DTOs;

/// <summary>
/// Represents an evaluation request.
/// </summary>
public class EvaluationRequest
{
    public EvaluationRequest(string namespaceKey, string flagKey, string entityId, Dictionary<string, string> context)
    {
        NamespaceKey = namespaceKey;
        FlagKey = flagKey;
        EntityId = entityId;
        Context = context;
    }
    public string? RequestId { get; set; }

    [Required]
    public string NamespaceKey { get; set; }

    [Required]
    public string FlagKey { get; set; }

    [Required]
    public string EntityId { get; set; }

    [Required]
    public Dictionary<string, string> Context { get; set; }

    public string? Reference { get; set; }
}