use flipt::api::FliptClient;
use flipt::evaluation::models::{
    BatchEvaluationRequest, ErrorEvaluationReason, EvaluationReason, EvaluationRequest,
    EvaluationResponseType,
};
use flipt::{ClientTokenAuthentication, ConfigBuilder};
use std::time::Duration;
use std::{collections::HashMap, env};
use url::Url;

#[tokio::test]
async fn tests() {
    let url = env::var("FLIPT_URL").unwrap();
    let token = env::var("FLIPT_AUTH_TOKEN").unwrap();

    let config = ConfigBuilder::default()
        .with_endpoint(Url::parse(&url).unwrap())
        .with_auth_strategy(ClientTokenAuthentication::new(token))
        .with_timeout(Duration::from_secs(60))
        .build();

    let flipt_client = FliptClient::new(config).unwrap();

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
    assert_eq!(variant.reason, EvaluationReason::Match);
    assert_eq!(variant.segment_keys.get(0).unwrap(), "segment1");

    let boolean = flipt_client
        .evaluation
        .boolean(&boolean_request)
        .await
        .unwrap();
    assert!(boolean.enabled);
    assert_eq!(boolean.flag_key, "flag_boolean");
    assert_eq!(boolean.reason, EvaluationReason::Match);

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
    assert_eq!(first_response.r#type, EvaluationResponseType::Variant);

    let variant = first_response.variant_response.clone().unwrap();
    assert!(variant.r#match);
    assert_eq!(variant.variant_key, "variant1");
    assert_eq!(variant.reason, EvaluationReason::Match);
    assert_eq!(variant.segment_keys.get(0).unwrap(), "segment1");

    // Boolean
    let second_response = batch.responses.get(1).unwrap();
    assert_eq!(second_response.r#type, EvaluationResponseType::Boolean);

    let boolean = second_response.boolean_response.clone().unwrap();
    assert!(boolean.enabled);
    assert_eq!(boolean.flag_key, "flag_boolean");
    assert_eq!(boolean.reason, EvaluationReason::Match);

    // Error
    let third_response = batch.responses.get(2).unwrap();
    assert_eq!(third_response.r#type, EvaluationResponseType::Error);

    let error = third_response.error_response.clone().unwrap();
    assert_eq!(error.flag_key, "notfound");
    assert_eq!(error.namespace_key, "default");
    assert_eq!(error.reason, ErrorEvaluationReason::NotFound);
}
