pub mod api;
pub mod error;
pub mod evaluation;
pub mod util;

use reqwest::header::HeaderMap;
use std::time::Duration;
// reexport
pub use url::Url;

#[derive(Debug, Clone)]
pub struct Config<T>
where
    T: AuthenticationStrategy,
{
    endpoint: Url,
    timeout: Duration,
    auth_strategy: Option<T>,
    headers: Option<HeaderMap>,
}

#[derive(Debug, Clone)]
pub struct ConfigBuilder<T>
where
    T: AuthenticationStrategy,
{
    endpoint: Option<Url>,
    auth_strategy: Option<T>,
    timeout: Option<Duration>,
    headers: Option<HeaderMap>,
}

impl<T: AuthenticationStrategy> Default for ConfigBuilder<T> {
    fn default() -> Self {
        Self {
            endpoint: Url::parse("http://localhost:8080").ok(),
            auth_strategy: None,
            timeout: Some(Duration::from_secs(60)), // Default timeout is 60 seconds
            headers: None,
        }
    }
}

impl<T: AuthenticationStrategy> ConfigBuilder<T> {
    pub fn with_endpoint(mut self, endpoint: Url) -> Self {
        self.endpoint = Some(endpoint);
        self
    }

    pub fn with_auth_strategy(mut self, auth_strategy: T) -> Self {
        self.auth_strategy = Some(auth_strategy);
        self
    }

    pub fn with_timeout(mut self, timeout: Duration) -> Self {
        self.timeout = Some(timeout);
        self
    }

    pub fn with_headers(mut self, headers: HeaderMap) -> Self {
        self.headers = Some(headers);
        self
    }

    pub fn build(self) -> Config<T> {
        Config {
            endpoint: self.endpoint.unwrap(),
            auth_strategy: self.auth_strategy,
            timeout: self.timeout.unwrap(),
            headers: self.headers,
        }
    }
}

pub trait AuthenticationStrategy {
    fn authenticate(self) -> HeaderMap;
}

pub struct NoneAuthentication {}

impl NoneAuthentication {
    pub fn new() -> Self {
        Self {}
    }
}

impl Default for NoneAuthentication {
    fn default() -> Self {
        Self::new()
    }
}

impl AuthenticationStrategy for NoneAuthentication {
    fn authenticate(self) -> HeaderMap {
        HeaderMap::new()
    }
}

pub struct JWTAuthentication {
    jwt_token: String,
}

impl JWTAuthentication {
    pub fn new(jwt_token: String) -> Self {
        Self { jwt_token }
    }
}

impl AuthenticationStrategy for JWTAuthentication {
    fn authenticate(self) -> HeaderMap {
        let mut header_map = HeaderMap::new();

        header_map.insert(
            "Authorization",
            format!("JWT {}", self.jwt_token).parse().unwrap(),
        );

        header_map
    }
}

pub struct ClientTokenAuthentication {
    client_token: String,
}

impl ClientTokenAuthentication {
    pub fn new(client_token: String) -> Self {
        Self { client_token }
    }
}

impl AuthenticationStrategy for ClientTokenAuthentication {
    fn authenticate(self) -> HeaderMap {
        let mut header_map = HeaderMap::new();

        header_map.insert(
            "Authorization",
            format!("Bearer {}", self.client_token).parse().unwrap(),
        );

        header_map
    }
}
