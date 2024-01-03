use crate::error::UpstreamError;

pub async fn deserialize<T: serde::de::DeserializeOwned>(
    resp: reqwest::Response,
) -> anyhow::Result<T> {
    if resp.status().is_success() {
        return Ok(resp.json::<T>().await?);
    }
    let parsed_err = resp.json::<UpstreamError>().await?;
    Err(anyhow::Error::new(parsed_err))
}
