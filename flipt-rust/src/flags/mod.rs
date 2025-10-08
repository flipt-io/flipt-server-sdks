pub mod models;

use crate::{error::UpstreamError, models::ListParameters, util::deserialize};
use models::{Flag, ListFlagsResponse};
use reqwest::Client;
use url::Url;

pub struct Flags {
    client: Client,
    url: Url,
}

impl Flags {
    pub fn new(client: Client, url: Url) -> Self {
        Self { client, url }
    }

    pub async fn get_flag(
        &self,
        namespace_key: &str,
        flag_key: &str,
    ) -> Result<Flag, UpstreamError> {
        let endpoint = format!(
            "{}api/v1/namespaces/{}/flags/{}",
            self.url.as_str(),
            namespace_key,
            flag_key
        );

        let response = match self.client.get(endpoint).send().await {
            Ok(r) => r,
            Err(e) => {
                return Err(UpstreamError::default_with_message(e.to_string()));
            }
        };

        deserialize(response).await
    }

    pub async fn list_flags(
        &self,
        namespace_key: &str,
        params: Option<&ListParameters>,
    ) -> Result<ListFlagsResponse, UpstreamError> {
        let endpoint = format!(
            "{}api/v1/namespaces/{}/flags",
            self.url.as_str(),
            namespace_key
        );

        let mut request = self.client.get(endpoint);

        if let Some(p) = params {
            request = request.query(p);
        }

        let response = match request.send().await {
            Ok(r) => r,
            Err(e) => {
                return Err(UpstreamError::default_with_message(e.to_string()));
            }
        };

        deserialize(response).await
    }
}
