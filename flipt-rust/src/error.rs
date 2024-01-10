use serde::Deserialize;
use std::{
    error::Error,
    fmt::{Display, Formatter, Result},
};

#[derive(Debug, Clone, Deserialize)]
#[non_exhaustive]
pub struct UpstreamError {
    pub code: i32,
    pub message: String,
    pub details: Option<Vec<serde_json::Value>>,
}

impl Default for UpstreamError {
    fn default() -> Self {
        Self {
            code: 0,
            message: "internal error".into(),
            details: Some(Vec::new()),
        }
    }
}

impl UpstreamError {
    pub fn default_with_message(message: String) -> Self {
        Self {
            code: 0,
            message,
            details: Some(Vec::new()),
        }
    }
}

impl Error for UpstreamError {}

impl Display for UpstreamError {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.message)?;
        if let Some(details) = &self.details.as_ref().filter(|d| !d.is_empty()) {
            write!(f, "\nDetails:")?;
            for error in details.iter() {
                write!(f, "\n- {error}")?;
            }
        }
        Ok(())
    }
}

#[derive(Debug, Clone)]
pub struct ClientError {
    pub message: String,
}

impl ClientError {
    pub fn new(message: String) -> Self {
        Self { message }
    }
}

impl Error for ClientError {}

impl Display for ClientError {
    fn fmt(&self, f: &mut Formatter) -> std::fmt::Result {
        write!(f, "Client error: {}", self.message)
    }
}
