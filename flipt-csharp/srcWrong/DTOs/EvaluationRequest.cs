using System.ComponentModel.DataAnnotations;

namespace FliptCsharp.DTOs;

public class EvaluationRequest
{
    public string requestId { get; set; }

    [Required]
    public string namespaceKey { get; set; }

    [Required]
    public string flagKey { get; set; }

    [Required]
    public string entityId { get; set; }

    [Required]
    public Dictionary<string, string> context { get; set; }

    public string reference { get; set; }
}