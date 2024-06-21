using System.ComponentModel.DataAnnotations;
using FliptCSharp.Models;

namespace FliptCSharp.DTOs;

/// <summary>
/// Represents a boolean evaluation response.
/// </summary>
public class BooleanEvaluationResponse
{
    [Required]
    public required string RequestId { get; set; }
    [Required]
    public required string FlagKey { get; set; }
    [Required]
    public bool Enabled { get; set; }
    [Required]
    public DateTime Timestamp { get; set; }
    [Required]
    public float RequestDurationMillis { get; set; }
    [Required]
    public Reason Reason { get; set; }
}
