use flipt::api::FliptClient;
use flipt::evaluation::models::{BatchEvaluationRequest, EvaluationRequest};
use flipt::{AuthScheme, Config};
use std::{collections::HashMap, env};
use url::Url;

#[tokio::test]
async fn tests() {
    let url = env::var("FLIPT_URL").unwrap();
    let token = env::var("FLIPT_AUTH_TOKEN").unwrap();

    let flipt_client = FliptClient::new(Config::new(
        Url::parse(&url).unwrap(),
        AuthScheme::BearerToken(token),
        60,
    ))
    .unwrap();

    let mut context: HashMap<String, String> = HashMap::new();
    context.insert("fizz".into(), "buzz".into());

    let variant_request = EvaluationRequest {
        namespace_key: "default".into(),
        flag_key: "flag1".into(),
        entity_id: "entity".into(),
        context: context.clone(),
        reference: None,
    };
    let boolean_request = EvaluationRequest {
        namespace_key: "default".into(),
        flag_key: "flag_boolean".into(),
        entity_id: "entity".into(),
        context: context.clone(),
        reference: None,
    };

    let variant = flipt_client
        .evaluation
        .variant(&variant_request)
        .await
        .unwrap();

    assert!(variant.r#match);
    assert_eq!(variant.variant_key, "variant1");
    assert_eq!(variant.reason, "MATCH_EVALUATION_REASON");
    assert_eq!(variant.segment_keys.get(0).unwrap(), "segment1");

    let boolean = flipt_client
        .evaluation
        .boolean(&boolean_request)
        .await
        .unwrap();
    assert!(boolean.enabled);
    assert_eq!(boolean.flag_key, "flag_boolean");
    assert_eq!(boolean.reason, "MATCH_EVALUATION_REASON");

    let mut requests: Vec<EvaluationRequest> = Vec::new();
    requests.push(variant_request);
    requests.push(boolean_request);
    requests.push(EvaluationRequest {
        namespace_key: "default".into(),
        flag_key: "notfound".into(),
        entity_id: "entity".into(),
        context: context.clone(),
        reference: None,
    });

    let batch_request = BatchEvaluationRequest {
        requests,
        reference: None,
    };
    let batch = flipt_client.evaluation.batch(&batch_request).await.unwrap();

    // Variant
    let first_response = batch.responses.get(0).unwrap();
    assert_eq!(first_response.r#type, "VARIANT_EVALUATION_RESPONSE_TYPE");

    let variant = first_response.variant_response.clone().unwrap();
    assert!(variant.r#match);
    assert_eq!(variant.variant_key, "variant1");
    assert_eq!(variant.reason, "MATCH_EVALUATION_REASON");
    assert_eq!(variant.segment_keys.get(0).unwrap(), "segment1");

    // Boolean
    let second_response = batch.responses.get(1).unwrap();
    assert_eq!(second_response.r#type, "BOOLEAN_EVALUATION_RESPONSE_TYPE");

    let boolean = second_response.boolean_response.clone().unwrap();
    assert!(boolean.enabled);
    assert_eq!(boolean.flag_key, "flag_boolean");
    assert_eq!(boolean.reason, "MATCH_EVALUATION_REASON");

    // Error
    let third_response = batch.responses.get(2).unwrap();
    assert_eq!(third_response.r#type, "ERROR_EVALUATION_RESPONSE_TYPE");

    let error = third_response.error_response.clone().unwrap();
    assert_eq!(error.flag_key, "notfound");
    assert_eq!(error.namespace_key, "default");
    assert_eq!(error.reason, "NOT_FOUND_ERROR_EVALUATION_REASON");
}
