pub mod api;
pub mod error;
pub mod evaluation;
pub mod util;

use reqwest::header::HeaderMap;
use url::Url;

#[derive(Debug, Clone)]
pub struct Config<T>
where
    T: AuthenticationStrategy,
{
    endpoint: Url,
    auth_strategy: Option<T>,
    timeout: u64,
}

impl<T> Default for Config<T>
where
    T: AuthenticationStrategy,
{
    fn default() -> Self {
        Self {
            endpoint: Url::parse("http://localhost:8080").unwrap(),
            auth_strategy: None,
            timeout: 60,
        }
    }
}

impl<T> Config<T>
where
    T: AuthenticationStrategy,
{
    pub fn new(endpoint: Url, auth_strategy: T, timeout: u64) -> Self {
        Self {
            endpoint,
            auth_strategy: Some(auth_strategy),
            timeout,
        }
    }
}

pub trait AuthenticationStrategy {
    fn authenticate(self) -> HeaderMap;
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

#[derive(Debug, Clone)]
pub enum AuthScheme {
    None,
    BearerToken(String),
    JWT(String),
}

impl Default for AuthScheme {
    fn default() -> Self {
        Self::None
    }
}
