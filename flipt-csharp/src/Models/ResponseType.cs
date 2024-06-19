using System.Runtime.Serialization;

namespace FliptCSharp.Models;

/// <summary>
/// Represents the response type.
/// </summary>
public enum ResponseType
{
    [EnumMember(Value = "VARIANT_EVALUATION_RESPONSE_TYPE")]
    VariantEvaluationResponseType,

    [EnumMember(Value = "BOOLEAN_EVALUATION_RESPONSE_TYPE")]
    BooleanEvaluationResponseType,

    [EnumMember(Value = "ERROR_EVALUATION_RESPONSE_TYPE")]
    ErrorEvaluationResponseType
}
