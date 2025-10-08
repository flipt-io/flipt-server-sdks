export interface FlagRequest {
  reference?: string;
}

export interface ListFlagsRequest extends FlagRequest {
  limit?: number;
  offset?: number;
  pageToken?: string;
}

export type FlagType = "VARIANT_FLAG_TYPE" | "BOOLEAN_FLAG_TYPE";

export interface Variant {
  attachment: string;
  description: string;
  flagKey: string;
  id: string;
  key: string;
  name: string;
  namespaceKey: string;
  createdAt: string;
  updatedAt: string;
}

export interface Flag {
  key: string;
  name: string;
  description: string;
  enabled: boolean;
  namespaceKey: string;
  type: FlagType;
  createdAt: string;
  updatedAt: string;
  variants: Variant[];
}

export interface ListFlagsResponse {
  flags: Flag[];
  nextPageToken: string;
  totalCount: number;
}
