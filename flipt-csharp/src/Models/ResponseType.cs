using System.Runtime.Serialization;
using System.Text.Json.Serialization;

namespace Flipt.Models;

/// <summary>
/// Represents the response type.
/// </summary>
[JsonConverter(typeof(JsonStringEnumConverter))]
public enum ResponseType
{
    [EnumMember(Value = "VARIANT_EVALUATION_RESPONSE_TYPE")]
    VariantEvaluationResponseType,

    [EnumMember(Value = "BOOLEAN_EVALUATION_RESPONSE_TYPE")]
    BooleanEvaluationResponseType,

    [EnumMember(Value = "ERROR_EVALUATION_RESPONSE_TYPE")]
    ErrorEvaluationResponseType
}
