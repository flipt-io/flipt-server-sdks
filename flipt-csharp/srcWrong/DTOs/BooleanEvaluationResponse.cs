using System.ComponentModel.DataAnnotations;
using FliptCsharp.Models;

namespace FliptCsharp.DTOs;

public class BooleanEvaluationResponse
{
    [Required]
    public string requestId { get; set; }
    [Required]
    public string flagKey { get; set; }
    [Required]
    public bool enabled { get; set; }
    [Required]
    public DateTime timestamp { get; set; }
    [Required]
    public int requestDurationMillis { get; set; }
    [Required]
    public Reason reason { get; set; }
}
