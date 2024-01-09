export interface EvaluationRequest {
  namespaceKey: string;
  flagKey: string;
  entityId: string;
  context: object;
}

export interface VariantEvaluationResponse {
  match: boolean;
  segmentKeys: string[];
  reason: string;
  flagKey: string;
  variantKey: string;
  variantAttachment: string;
  requestDurationMillis: number;
  timestamp: string;
}

export interface BooleanEvaluationResponse {
  enabled: boolean;
  flagKey: string;
  reason: string;
  requestDurationMillis: number;
  timestamp: string;
}

export interface BatchEvaluationRequest {
  request_id?: string;
  requests: EvaluationRequest[];
}

interface ErrorEvaluationResponse {
  flagKey: string;
  namespaceKey: string;
  reason: string;
}

export interface EvaluationResponse {
  type: string;
  booleanResponse?: BooleanEvaluationResponse;
  variantResponse?: VariantEvaluationResponse;
  errorResponse?: ErrorEvaluationResponse;
}

export interface BatchEvaluationResponse {
  requestId: string;
  responses: EvaluationResponse[];
  requestDurationMillis: number;
}
