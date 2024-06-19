using System.Runtime.Serialization;

namespace FliptCsharp.Models;

public enum Reason
{
    [EnumMember(Value = "UNKNOWN_EVALUATION_REASON")]
    UNKNOWN_EVALUATION_REASON,

    [EnumMember(Value = "FLAG_DISABLED_EVALUATION_REASON")]
    FLAG_DISABLED_EVALUATION_REASON,

    [EnumMember(Value = "MATCH_EVALUATION_REASON")]
    MATCH_EVALUATION_REASON,

    [EnumMember(Value = "DEFAULT_EVALUATION_REASON")]
    DEFAULT_EVALUATION_REASON
}