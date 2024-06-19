using System.Text.Json.Serialization;

namespace FliptCsharp.Models;
using System.Runtime.Serialization;

public enum ResponseType
{
    [EnumMember(Value = "VARIANT_EVALUATION_RESPONSE_TYPE")]
    VARIANT_EVALUATION_RESPONSE_TYPE,

    [EnumMember(Value = "BOOLEAN_EVALUATION_RESPONSE_TYPE")]
    BOOLEAN_EVALUATION_RESPONSE_TYPE,

    [EnumMember(Value = "ERROR_EVALUATION_RESPONSE_TYPE")]
    ERROR_EVALUATION_RESPONSE_TYPE
}
