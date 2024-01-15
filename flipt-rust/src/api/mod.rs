use crate::error::ClientError;
use crate::evaluation::Evaluation;
use crate::{AuthenticationStrategy, Config, NoneAuthentication};
use std::time::Duration;

pub struct FliptClient {
    pub evaluation: Evaluation,
}

impl FliptClient {
    pub fn new<T>(config: Config<T>) -> Result<Self, ClientError>
    where
        T: AuthenticationStrategy,
    {
        let header_map = config.auth_strategy.authenticate();

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

impl Default for FliptClient {
    fn default() -> Self {
        Self::new::<NoneAuthentication>(Config::default()).unwrap()
    }
}
