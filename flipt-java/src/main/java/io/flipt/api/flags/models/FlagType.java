package io.flipt.api.flags.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlagType {
  VARIANT("VARIANT_FLAG_TYPE"),
  BOOLEAN("BOOLEAN_FLAG_TYPE");

  private final String value;

  FlagType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static FlagType fromValue(String value) {
    for (FlagType type : FlagType.values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown flag type: " + value);
  }
}
