package io.flipt.api.flags.models;

public enum FlagType {
  VARIANT("VARIANT_FLAG_TYPE"),
  BOOLEAN("BOOLEAN_FLAG_TYPE");

  private final String value;

  FlagType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static FlagType fromValue(String value) {
    for (FlagType type : FlagType.values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown flag type: " + value);
  }
}
