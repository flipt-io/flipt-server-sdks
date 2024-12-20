package io.flipt.api.flags.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class Flag {

  @JsonProperty("key")
  private String key;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("enabled")
  private boolean enabled;

  @JsonProperty("namespaceKey")
  private String namespaceKey;

  @JsonProperty("type")
  private FlagType type;

  @JsonProperty("createdAt")
  private LocalDateTime createdAt;

  @JsonProperty("updatedAt")
  private LocalDateTime updatedAt;

  @JsonProperty("variants")
  private List<Variant> variants;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getNamespaceKey() {
    return namespaceKey;
  }

  public void setNamespaceKey(String namespaceKey) {
    this.namespaceKey = namespaceKey;
  }

  public FlagType getType() {
    return type;
  }

  public void setType(FlagType type) {
    this.type = type;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<Variant> getVariants() {
    return variants;
  }

  public void setVariants(List<Variant> variants) {
    this.variants = variants;
  }
}

