export interface EvaluationRequest {
  namespaceKey: string;
  flagKey: string;
  entityId: string;
  context: object;
}

export interface VariantEvaluationResponse {
  match: boolean;
  segmentKeys: string[];
  reason: EvaluationReason;
  flagKey: string;
  variantKey: string;
  variantAttachment: string;
  requestDurationMillis: number;
  timestamp: string;
}

export interface BooleanEvaluationResponse {
  enabled: boolean;
  flagKey: string;
  reason: EvaluationReason;
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
  reason: ErrorEvaluationReason;
}

export interface EvaluationResponse {
  type: EvaluationResponseType;
  booleanResponse?: BooleanEvaluationResponse;
  variantResponse?: VariantEvaluationResponse;
  errorResponse?: ErrorEvaluationResponse;
}

export interface BatchEvaluationResponse {
  requestId: string;
  responses: EvaluationResponse[];
  requestDurationMillis: number;
}

export type EvaluationResponseType =
  | "VARIANT_EVALUATION_RESPONSE_TYPE"
  | "BOOLEAN_EVALUATION_RESPONSE_TYPE"
  | "ERROR_EVALUATION_RESPONSE_TYPE";

export type EvaluationReason =
  | "UNKNOWN_EVALUATION_REASON"
  | "FLAG_DISABLED_EVALUATION_REASON"
  | "MATCH_EVALUATION_REASON"
  | "DEFAULT_EVALUATION_REASON";

export type ErrorEvaluationReason =
  | "UNKNOWN_ERROR_EVALUATION_REASON"
  | "NOT_FOUND_ERROR_EVALUATION_REASON";
