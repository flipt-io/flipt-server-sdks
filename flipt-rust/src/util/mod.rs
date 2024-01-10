use crate::error::UpstreamError;

pub async fn deserialize<T: serde::de::DeserializeOwned>(
    resp: reqwest::Response,
) -> Result<T, UpstreamError> {
    if resp.status().is_success() {
        match resp.json::<T>().await {
            Ok(t) => {
                return Ok(t);
            }
            Err(err) => {
                return Err(UpstreamError::default_with_message(err.to_string()));
            }
        }
    }

    match resp.json::<UpstreamError>().await {
        Ok(ue) => Err(ue),
        Err(err) => Err(UpstreamError::default_with_message(err.to_string())),
    }
}
