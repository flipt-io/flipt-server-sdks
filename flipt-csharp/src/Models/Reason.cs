using System.Runtime.Serialization;

namespace FliptCSharp.Models;

/// <summary>
/// Represents the reason for the evaluation.
/// </summary>
public enum Reason
{
    [EnumMember(Value = "UNKNOWN_EVALUATION_REASON")]
    UnknownEvaluationReason,

    [EnumMember(Value = "FLAG_DISABLED_EVALUATION_REASON")]
    FlagDisabledEvaluationReason,

    [EnumMember(Value = "MATCH_EVALUATION_REASON")]
    MatchEvaluationReason,

    [EnumMember(Value = "DEFAULT_EVALUATION_REASON")]
    DefaultEvaluationReason
}