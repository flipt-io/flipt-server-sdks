use crate::error::ClientError;
use crate::evaluation::Evaluation;
use crate::{AuthenticationStrategy, Config, ConfigBuilder, NoneAuthentication};
use std::time::Duration;

pub struct FliptClient {
    pub evaluation: Evaluation,
}

impl FliptClient {
    pub fn new<T>(config: Config<T>) -> Result<Self, ClientError>
    where
        T: AuthenticationStrategy,
    {
        let mut header_map = config.headers.unwrap_or_default();

        if let Some(auth_strategy) = config.auth_strategy {
            let auth_headers = auth_strategy.authenticate();
            header_map.extend(auth_headers);
        }

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
            evaluation: Evaluation::new(client, config.endpoint, config.reference),
        })
    }
}

impl Default for FliptClient {
    fn default() -> Self {
        Self::new::<NoneAuthentication>(ConfigBuilder::<NoneAuthentication>::default().build())
            .unwrap()
    }
}
