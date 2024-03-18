import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.flipt.api.FliptClient;
import io.flipt.api.authentication.AuthenticationStrategy;
import io.flipt.api.authentication.ClientTokenAuthenticationStrategy;
import io.flipt.api.evaluation.models.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import models.BooleanTest;
import models.Corpus;
import models.VariantTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestFliptClient {
  private static FliptClient fliptClient;
  private static Corpus corpus;

  @BeforeAll
  static void initialize() throws FileNotFoundException, JsonProcessingException {
    String fliptURL = System.getenv().get("FLIPT_URL");
    String authToken = System.getenv().get("FLIPT_AUTH_TOKEN");

    assert fliptURL != null && !fliptURL.isEmpty();
    assert authToken != null && !authToken.isEmpty();

    AuthenticationStrategy authenticationStrategy =
        new ClientTokenAuthenticationStrategy(authToken);

    File testsJson = new File("tests.json");
    Scanner reader = new Scanner(testsJson);

    StringBuilder sb = new StringBuilder();

    while (reader.hasNextLine()) {
      sb.append(reader.nextLine());
    }

    ObjectMapper objectMapper =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    corpus = objectMapper.readValue(sb.toString(), Corpus.class);

    fliptClient =
        FliptClient.builder().url(fliptURL).authentication(authenticationStrategy).build();
  }

  @Test
  void testVariantEvaluation() {
    for (VariantTest variantTest : corpus.getVariantTests()) {
      EvaluationRequest request = variantTest.getRequest();
      VariantEvaluationResponse expectationResponse = variantTest.getExpectation();
      VariantEvaluationResponse variantResponse = fliptClient.evaluation().evaluateVariant(request);

      Assertions.assertEquals(expectationResponse.getFlagKey(), variantResponse.getFlagKey());
      Assertions.assertEquals(expectationResponse.getReason(), variantResponse.getReason());
      Assertions.assertEquals(expectationResponse.getVariantKey(), variantResponse.getVariantKey());

      for (String segmentKey : variantResponse.getSegmentKeys()) {
        Assertions.assertTrue(expectationResponse.getSegmentKeys().contains(segmentKey));
      }
    }
  }

  @Test
  void testBooleanEvaluation() {
    for (BooleanTest booleanTest : corpus.getBooleanTests()) {
      EvaluationRequest request = booleanTest.getRequest();
      BooleanEvaluationResponse expectationResponse = booleanTest.getExpectation();
      BooleanEvaluationResponse booleanResponse = fliptClient.evaluation().evaluateBoolean(request);

      Assertions.assertEquals(expectationResponse.isEnabled(), booleanResponse.isEnabled());
      Assertions.assertEquals(expectationResponse.getReason(), booleanResponse.getReason());
      Assertions.assertEquals(expectationResponse.getFlagKey(), booleanResponse.getFlagKey());
    }
  }
}
