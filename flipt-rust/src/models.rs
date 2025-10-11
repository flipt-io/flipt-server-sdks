use serde::Serialize;

#[derive(Default, Serialize)]
pub struct CommonParameters {
    pub reference: Option<String>,
}

#[derive(Default, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ListParameters {
    #[serde(flatten)]
    pub common: CommonParameters,
    pub limit: Option<i32>,
    pub offset: Option<i32>,
    pub page_token: Option<String>,
}

use serde::Deserialize;

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct PaginatedResponse {
    pub next_page_token: String,
    pub total_count: i32,
}
