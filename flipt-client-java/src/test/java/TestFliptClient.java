/* (C) 2024 */
import com.flipt.api.FliptClient;
import com.flipt.api.evaluation.models.*;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFliptClient {
  @Test
  void testVariant() {
    String fliptURL = System.getenv().get("FLIPT_URL");
    String authToken = System.getenv().get("FLIPT_AUTH_TOKEN");

    assert !fliptURL.isEmpty();
    assert !authToken.isEmpty();

    FliptClient fc = FliptClient.builder().url(fliptURL).token(authToken).build();

    Map<String, String> context = new HashMap<>();
    context.put("fizz", "buzz");
    VariantEvaluationResponse variant =
        fc.evaluation.variant(new EvaluationRequest("default", "flag1", "entity", context));

    Assertions.assertTrue(variant.isMatch());
    Assertions.assertEquals("flag1", variant.getFlagKey());
    Assertions.assertEquals("MATCH_EVALUATION_REASON", variant.getReason());
    Assertions.assertEquals("variant1", variant.getVariantKey());
    Assertions.assertEquals("segment1", variant.getSegmentKeys().getFirst());
  }

  @Test
  void testBoolean() {
    String fliptURL = System.getenv().get("FLIPT_URL");
    String authToken = System.getenv().get("FLIPT_AUTH_TOKEN");

    assert !fliptURL.isEmpty();
    assert !authToken.isEmpty();

    FliptClient fc = FliptClient.builder().url(fliptURL).token(authToken).build();

    Map<String, String> context = new HashMap<>();
    context.put("fizz", "buzz");

    BooleanEvaluationResponse booleanEvaluation =
        fc.evaluation.booleanEvaluation(
            new EvaluationRequest("default", "flag_boolean", "entity", context));

    Assertions.assertTrue(booleanEvaluation.isEnabled());
    Assertions.assertEquals("flag_boolean", booleanEvaluation.getFlagKey());
    Assertions.assertEquals("MATCH_EVALUATION_REASON", booleanEvaluation.getReason());
  }

  @Test
  void testBatch() {
    String fliptURL = System.getenv().get("FLIPT_URL");
    String authToken = System.getenv().get("FLIPT_AUTH_TOKEN");

    assert !fliptURL.isEmpty();
    assert !authToken.isEmpty();

    FliptClient fc = FliptClient.builder().url(fliptURL).token(authToken).build();

    Map<String, String> context = new HashMap<>();
    context.put("fizz", "buzz");

    EvaluationRequest variantEvaluationRequest =
        new EvaluationRequest("default", "flag1", "entity", context);
    EvaluationRequest booleanEvaluationRequest =
        new EvaluationRequest("default", "flag_boolean", "entity", context);
    EvaluationRequest errorEvaluationRequest =
        new EvaluationRequest("default", "flag1234", "entity", new HashMap<>());

    List<EvaluationRequest> evaluationRequests = new ArrayList<>();
    evaluationRequests.add(variantEvaluationRequest);
    evaluationRequests.add(booleanEvaluationRequest);
    evaluationRequests.add(errorEvaluationRequest);

    BatchEvaluationResponse batch =
        fc.evaluation.batch(new BatchEvaluationRequest(Optional.of(""), evaluationRequests));

    // Variant
    EvaluationResponse first = batch.getResponses().getFirst();
    Assertions.assertEquals("VARIANT_EVALUATION_RESPONSE_TYPE", first.getType());

    VariantEvaluationResponse variant = first.getVariantResponse().get();
    Assertions.assertTrue(variant.isMatch());
    Assertions.assertEquals("flag1", variant.getFlagKey());
    Assertions.assertEquals("MATCH_EVALUATION_REASON", variant.getReason());
    Assertions.assertEquals("variant1", variant.getVariantKey());
    Assertions.assertEquals("segment1", variant.getSegmentKeys().getFirst());

    // Boolean
    EvaluationResponse second = batch.getResponses().get(1);
    Assertions.assertEquals("BOOLEAN_EVALUATION_RESPONSE_TYPE", second.getType());

    BooleanEvaluationResponse booleanEvaluation = second.getBooleanResponse().get();
    Assertions.assertTrue(booleanEvaluation.isEnabled());
    Assertions.assertEquals("flag_boolean", booleanEvaluation.getFlagKey());
    Assertions.assertEquals("MATCH_EVALUATION_REASON", booleanEvaluation.getReason());

    // Error
    EvaluationResponse third = batch.getResponses().get(2);
    Assertions.assertEquals("ERROR_EVALUATION_RESPONSE_TYPE", third.getType());

    ErrorEvaluationResponse errorEvaluation = third.getErrorResponse().get();
    Assertions.assertEquals("flag1234", errorEvaluation.getFlagKey());
    Assertions.assertEquals("default", errorEvaluation.getNamespaceKey());
    Assertions.assertEquals("NOT_FOUND_ERROR_EVALUATION_REASON", errorEvaluation.getReason());
  }
}
