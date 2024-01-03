pub mod error;
pub mod evaluation;
pub mod util;

use evaluation::Evaluation;
use reqwest::header::HeaderMap;
use std::time::Duration;
use url::Url;

pub struct FliptClient {
    pub evaluation: Evaluation,
}

impl FliptClient {
    pub fn new(url: Url, token: String, timeout: u64) -> anyhow::Result<Self> {
        let mut header_map = HeaderMap::new();

        if !token.is_empty() {
            header_map.insert(
                "Authorization",
                format!("Bearer {}", token).parse().unwrap(),
            );
        }

        let client = reqwest::Client::builder()
            .timeout(Duration::from_secs(timeout))
            .default_headers(header_map)
            .build()?;

        Ok(Self {
            evaluation: Evaluation::new(client, url),
        })
    }
}

impl Default for FliptClient {
    fn default() -> Self {
        Self::new(Url::parse("http://localhost:8080").unwrap(), "".into(), 60).unwrap()
    }
}
