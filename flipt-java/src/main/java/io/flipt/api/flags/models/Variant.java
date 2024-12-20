package io.flipt.api.flags.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Variant {

  @JsonProperty("attachment")
  private String attachment;

  @JsonProperty("description")
  private String description;

  @JsonProperty("flagKey")
  private String flagKey;

  @JsonProperty("id")
  private String id;

  @JsonProperty("key")
  private String key;

  @JsonProperty("name")
  private String name;

  @JsonProperty("namespaceKey")
  private String namespaceKey;

  @JsonProperty("createdAt")
  private LocalDateTime createdAt;

  @JsonProperty("updatedAt")
  private LocalDateTime updatedAt;

  public String getAttachment() {
    return attachment;
  }

  public void setAttachment(String attachment) {
    this.attachment = attachment;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFlagKey() {
    return flagKey;
  }

  public void setFlagKey(String flagKey) {
    this.flagKey = flagKey;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public String getNamespaceKey() {
    return namespaceKey;
  }

  public void setNamespaceKey(String namespaceKey) {
    this.namespaceKey = namespaceKey;
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
}

