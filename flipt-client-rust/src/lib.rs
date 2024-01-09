pub mod api;
pub mod error;
pub mod evaluation;
pub mod util;

use url::Url;

#[derive(Debug, Clone)]
pub struct Config {
    endpoint: Url,
    auth_scheme: AuthScheme,
    timeout: u64,
}

impl Default for Config {
    fn default() -> Self {
        Self {
            endpoint: Url::parse("http://localhost:8080").unwrap(),
            auth_scheme: AuthScheme::None,
            timeout: 60,
        }
    }
}

impl Config {
    pub fn new(endpoint: Url, auth_scheme: AuthScheme, timeout: u64) -> Self {
        Self {
            endpoint,
            auth_scheme,
            timeout,
        }
    }
}

#[derive(Debug, Clone)]
pub enum AuthScheme {
    None,
    BearerToken(String),
    JWTToken(String),
}

impl Default for AuthScheme {
    fn default() -> Self {
        Self::None
    }
}
