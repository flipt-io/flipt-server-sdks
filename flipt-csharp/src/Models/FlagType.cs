using System.Runtime.Serialization;
using System.Text.Json.Serialization;

namespace Flipt.Models;

/// <summary>
/// Represents the type of flag.
/// </summary>
[JsonConverter(typeof(JsonStringEnumConverter))]
public enum FlagType
{
    [EnumMember(Value = "VARIANT_FLAG_TYPE")]
    VariantFlagType,

    [EnumMember(Value = "BOOLEAN_FLAG_TYPE")]
    BooleanFlagType
}
