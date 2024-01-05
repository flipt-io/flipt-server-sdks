package com.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BatchEvaluationRequest {
    private final Optional<String> requestId;
    private final List<EvaluationRequest> requests;

    public BatchEvaluationRequest(Optional<String> requestId, List<EvaluationRequest> requests) {
        this.requestId = requestId;
        this.requests = requests;
    }

    @JsonProperty("requests")
    public List<EvaluationRequest> getRequests() {
        return requests;
    }

    @JsonProperty("requestId")
    public Optional<String> getRequestId() {
        return requestId;
    }
}
