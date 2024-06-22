using System.ComponentModel.DataAnnotations;
using Flipt.Models;

namespace Flipt.DTOs;

/// <summary>
/// Represents a variant evaluation response.
/// </summary>
public class VariantEvaluationResponse
{
    [Required]
    public required string RequestId { get; set; }
    [Required]
    public bool Match { get; set; }
    [Required]
    public required string FlagKey { get; set; }
    [Required]
    public required List<string> SegmentKeys { get; set; }
    [Required]
    public required string VariantKey { get; set; }
    [Required]
    public required string VariantAttachment { get; set; }
    [Required]
    public DateTime Timestamp { get; set; }
    [Required]
    public float RequestDurationMillis { get; set; }
    [Required]
    public Reason Reason { get; set; }
}
