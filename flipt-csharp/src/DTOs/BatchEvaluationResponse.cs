using System.ComponentModel.DataAnnotations;
using FliptCSharp.Models;

namespace FliptCSharp.DTOs;

/// <summary>
/// Represents a batch evaluation response.
/// </summary>
public class BatchEvaluationResponse
{
    [Required]
    public required string RequestId { get; set; }

    [Required]
    public required Response[] Responses { get; set; }

    [Required]
    public float RequestDurationMillis { get; set; }
}


public class Response
{
    [Required]
    public ResponseType Type { get; set; }
    public BooleanEvaluationResponse? BooleanResponse { get; set; }
    public VariantEvaluationResponse? VariantResponse { get; set; }
    public ErrorEvaluationResponse? ErrorResponse { get; set; }
}


public class ErrorEvaluationResponse
{
    public string? FlagKey { get; set; }
    public string? NamespaceKey { get; set; }
    public string? Reason { get; set; }
}
