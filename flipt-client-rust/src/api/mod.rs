use crate::error::ClientError;
use crate::evaluation::Evaluation;
use crate::{AuthScheme, Config};
use reqwest::header::HeaderMap;
use std::time::Duration;

pub struct ApiClient {
    pub evaluation: Evaluation,
}

impl ApiClient {
    pub fn new(config: Config) -> Result<Self, ClientError> {
        let mut header_map = HeaderMap::new();

        match config.auth_scheme {
            AuthScheme::BearerToken(bearer) => {
                header_map.insert(
                    "Authorization",
                    format!("Bearer {}", bearer).parse().unwrap(),
                );
            }
            AuthScheme::JWTToken(jwt) => {
                header_map.insert("Authorization", format!("JWT {}", jwt).parse().unwrap());
            }
            AuthScheme::None => {}
        };

        let client = match reqwest::Client::builder()
            .timeout(Duration::from_secs(config.timeout))
            .default_headers(header_map)
            .build()
        {
            Ok(client) => client,
            Err(e) => {
                return Err(ClientError::new(e.to_string()));
            }
        };

        Ok(Self {
            evaluation: Evaluation::new(client, config.endpoint),
        })
    }
}

impl Default for ApiClient {
    fn default() -> Self {
        Self::new(Config::default()).unwrap()
    }
}
