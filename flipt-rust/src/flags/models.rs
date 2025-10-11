use chrono::{DateTime, Utc};
use serde::Deserialize;

#[derive(Debug, Clone, PartialEq, Deserialize)]
pub enum FlagType {
    #[serde(rename = "VARIANT_FLAG_TYPE")]
    Variant,
    #[serde(rename = "BOOLEAN_FLAG_TYPE")]
    Boolean,
}

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Variant {
    pub attachment: String,
    pub description: String,
    pub flag_key: String,
    pub id: String,
    pub key: String,
    pub name: String,
    pub namespace_key: String,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Flag {
    pub key: String,
    pub name: String,
    pub description: String,
    pub enabled: bool,
    pub namespace_key: String,
    pub r#type: FlagType,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
    pub variants: Vec<Variant>,
}

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct ListFlagsResponse {
    pub flags: Vec<Flag>,
    pub next_page_token: String,
    pub total_count: i32,
}
