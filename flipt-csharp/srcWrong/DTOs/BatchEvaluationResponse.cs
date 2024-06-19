using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using FliptCsharp.Models;

namespace FliptCsharp.DTOs;

public class BatchEvaluationResponse
{
    [Required]
    public string requestId { get; set; }
    [Required]
    public Response[] responses { get; set; }
    [Required]
    public int requestDurationMillis { get; set; }
}


public class Response
{
    [Required]
    public ResponseType type { get; set; }
    public BooleanEvaluationResponse booleanResponse { get; set; }
    public VariantEvaluationResponse variantResponse { get; set; }
    public ErrorEvaluationResponse errorResponse { get; set; }
}


public class ErrorEvaluationResponse
{
    public string flagKey { get; set; }
    public string namespaceKey { get; set; }
    public string reason { get; set; }
}
