use flipt::api::FliptClient;
use flipt::evaluation::models::{
    BooleanEvaluationResponse, EvaluationRequest, VariantEvaluationResponse,
};
use flipt::{ClientTokenAuthentication, Config};
use serde::Deserialize;
use std::env;
use std::fs::File;
use std::io::BufReader;
use url::Url;

#[derive(Deserialize)]
struct Corpus {
    #[serde(rename = "VARIANT")]
    variant: Vec<VariantTest>,
    #[serde(rename = "BOOLEAN")]
    boolean: Vec<BooleanTest>,
}

#[derive(Deserialize)]
struct VariantTest {
    request: EvaluationRequest,
    expectation: VariantEvaluationResponse,
}

#[derive(Deserialize)]
struct BooleanTest {
    request: EvaluationRequest,
    expectation: BooleanEvaluationResponse,
}

#[tokio::test]
async fn tests() {
    let url = env::var("FLIPT_URL").unwrap();
    let token = env::var("FLIPT_AUTH_TOKEN").unwrap();

    let flipt_client = FliptClient::new(Config::new(
        Url::parse(&url).unwrap(),
        ClientTokenAuthentication::new(token),
        60,
    ))
    .unwrap();

    let file = File::open("tests.json").expect("tests.json should be read properly");

    let corpus: Corpus = serde_json::from_reader(BufReader::new(file))
        .expect("tests.json should deserialize properly");

    for variant_test in corpus.variant.into_iter() {
        let expectation = variant_test.expectation;
        let variant = flipt_client
            .evaluation
            .variant(&variant_test.request)
            .await
            .unwrap();
        assert_eq!(variant.flag_key, expectation.flag_key);
        assert_eq!(variant.r#match, expectation.r#match);
        assert_eq!(variant.reason, expectation.reason);
        assert_eq!(variant.variant_key, expectation.variant_key);
        for elem in variant.segment_keys.into_iter() {
            assert!(expectation.segment_keys.contains(&elem));
        }
    }

    for boolean_test in corpus.boolean.into_iter() {
        let expectation = boolean_test.expectation;

        let boolean = flipt_client
            .evaluation
            .boolean(&boolean_test.request)
            .await
            .unwrap();
        assert_eq!(boolean.enabled, expectation.enabled);
        assert_eq!(boolean.flag_key, expectation.flag_key);
        assert_eq!(boolean.reason, expectation.reason);
    }
}
