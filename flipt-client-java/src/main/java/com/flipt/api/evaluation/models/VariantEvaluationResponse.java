package com.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VariantEvaluationResponse {
    private final boolean match;
    private final String[] segmentKeys;
    private final String reason;
    private final String flagKey;
    private final String variantKey;
    private final String variantAttachment;
    private final float requestDurationMillis;
    private final String timestamp;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public VariantEvaluationResponse(@JsonProperty("match") boolean match, @JsonProperty("segmentKeys") String[] segmentKeys, @JsonProperty("reason") String reason, @JsonProperty("flagKey") String flagKey, @JsonProperty("variantKey") String variantKey, @JsonProperty("variantAttachment") String variantAttachment, @JsonProperty("requestDurationMillis") float requestDurationMillis, @JsonProperty("timestamp") String timestamp) {
        this.match = match;
        this.segmentKeys = segmentKeys;
        this.reason = reason;
        this.flagKey = flagKey;
        this.variantKey = variantKey;
        this.variantAttachment = variantAttachment;
        this.requestDurationMillis = requestDurationMillis;
        this.timestamp = timestamp;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("segmentKeys")
    public String[] getSegmentKeys() {
        return segmentKeys;
    }

    @JsonProperty("requestDurationMillis")
    public float getRequestDurationMillis() {
        return requestDurationMillis;
    }

    @JsonProperty("variantAttachment")
    public String getVariantAttachment() {
        return variantAttachment;
    }

    @JsonProperty("variantKey")
    public String getVariantKey() {
        return variantKey;
    }

    @JsonProperty("flagKey")
    public String getFlagKey() {
        return flagKey;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("match")
    public boolean isMatch() {
        return match;
    }
}
