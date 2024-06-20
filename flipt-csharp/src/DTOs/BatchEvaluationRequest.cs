using System.ComponentModel.DataAnnotations;

namespace FliptCSharp.DTOs;

/// <summary>
/// Represents a batch evaluation request.
/// </summary>
public class BatchEvaluationRequest
{
    public BatchEvaluationRequest(List<EvaluationRequest> evaluationRequests)
    {
        EvaluationRequests = evaluationRequests;
    }
    public string? RequestId { get; set; }

    [Required]
    public List<EvaluationRequest> EvaluationRequests { get; set; }
    public string? Reference { get; set; }
}