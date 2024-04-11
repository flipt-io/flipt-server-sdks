pub mod models;

use crate::{error::UpstreamError, util::deserialize};
use models::{
    BatchEvaluationRequest, BatchEvaluationResponse, BooleanEvaluationResponse, EvaluationRequest,
    VariantEvaluationResponse,
};
use reqwest::Client;
use url::Url;

pub struct Evaluation {
    client: Client,
    url: Url,
    reference: Option<String>,
}

impl Evaluation {
    pub fn new(client: Client, url: Url, reference: Option<String>) -> Self {
        Self {
            client,
            url,
            reference,
        }
    }

    pub async fn boolean(
        &self,
        request: &EvaluationRequest,
    ) -> Result<BooleanEvaluationResponse, UpstreamError> {
        let endpoint = match self.reference {
            Some(ref r) => format!("{}evaluate/v1/boolean?reference={}", self.url.as_str(), r),
            None => format!("{}evaluate/v1/boolean", self.url.as_str()),
        };

        let response = match self.client.post(endpoint).json(request).send().await {
            Ok(r) => r,
            Err(e) => {
                return Err(UpstreamError::default_with_message(e.to_string()));
            }
        };

        deserialize(response).await
    }

    pub async fn variant(
        &self,
        request: &EvaluationRequest,
    ) -> Result<VariantEvaluationResponse, UpstreamError> {
        let endpoint = match self.reference {
            Some(ref r) => format!("{}evaluate/v1/variant?reference={}", self.url.as_str(), r),
            None => format!("{}evaluate/v1/variant", self.url.as_str()),
        };

        let response = match self.client.post(endpoint).json(request).send().await {
            Ok(r) => r,
            Err(e) => {
                return Err(UpstreamError::default_with_message(e.to_string()));
            }
        };

        deserialize(response).await
    }

    pub async fn batch(
        &self,
        batch: &BatchEvaluationRequest,
    ) -> Result<BatchEvaluationResponse, UpstreamError> {
        let endpoint = match self.reference {
            Some(ref r) => format!("{}evaluate/v1/batch?reference={}", self.url.as_str(), r),
            None => format!("{}evaluate/v1/batch", self.url.as_str()),
        };

        let response = match self.client.post(endpoint).json(batch).send().await {
            Ok(r) => r,
            Err(e) => {
                return Err(UpstreamError::default_with_message(e.to_string()));
            }
        };

        deserialize(response).await
    }
}
