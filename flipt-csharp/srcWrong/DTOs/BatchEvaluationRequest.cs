using System.ComponentModel.DataAnnotations;

namespace FliptCsharp.DTOs;

public class BatchEvaluationRequest
{
    public string requestId { get; set; }

    [Required]
    public List<EvaluationRequest> evaluationRequests { get; set; }
    public string reference { get; set; }
}