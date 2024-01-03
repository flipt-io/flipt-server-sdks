pub mod models;

use crate::util::deserialize;
use models::{
    BatchEvaluationRequest, BatchEvaluationResponse, BooleanEvaluationResponse, EvaluationRequest,
    VariantEvaluationResponse,
};
use reqwest::Client;
use url::Url;

pub struct Evaluation {
    client: Client,
    url: Url,
}

impl Evaluation {
    pub fn new(client: Client, url: Url) -> Self {
        Self { client, url }
    }

    pub async fn boolean(
        &self,
        request: &EvaluationRequest,
    ) -> anyhow::Result<BooleanEvaluationResponse> {
        let endpoint = format!("{}evaluate/v1/boolean", self.url.as_str());

        let response = self.client.post(endpoint).json(request).send().await?;

        deserialize(response).await
    }

    pub async fn variant(
        &self,
        request: &EvaluationRequest,
    ) -> anyhow::Result<VariantEvaluationResponse> {
        let endpoint = format!("{}evaluate/v1/variant", self.url.as_str());

        let response = self.client.post(endpoint).json(request).send().await?;

        deserialize(response).await
    }

    pub async fn batch(
        &self,
        batch: &BatchEvaluationRequest,
    ) -> anyhow::Result<BatchEvaluationResponse> {
        let endpoint = format!("{}evaluate/v1/batch", self.url.as_str());

        let response = self.client.post(endpoint).json(batch).send().await?;

        deserialize(response).await
    }
}
