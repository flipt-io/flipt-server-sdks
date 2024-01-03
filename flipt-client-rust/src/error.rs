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
