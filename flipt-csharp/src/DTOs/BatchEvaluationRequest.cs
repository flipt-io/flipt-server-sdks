using System.ComponentModel.DataAnnotations;

namespace FliptCSharp.DTOs;

/// <summary>
/// Represents a batch evaluation request.
/// </summary>
public class BatchEvaluationRequest
{
    public BatchEvaluationRequest(List<EvaluationRequest> requests)
    {
        Requests = requests;
    }
    public string? RequestId { get; set; }

    [Required]
    public List<EvaluationRequest> Requests { get; set; }
    public string? Reference { get; set; }
}