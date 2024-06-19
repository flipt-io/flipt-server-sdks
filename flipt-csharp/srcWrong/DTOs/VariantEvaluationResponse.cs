using FliptCsharp.Models;

namespace FliptCsharp.DTOs;

public class VariantEvaluationResponse
{
    public string requestId { get; set; }
    public bool match { get; set; }
    public string flagKey { get; set; }
    public List<string> segmentKeys { get; set; }
    public string variantKey { get; set; }
    public string variantAttachment { get; set; }
    public DateTime timestamp { get; set; }
    public int requestDurationMillis { get; set; }
    public Reason reason { get; set; }
}
