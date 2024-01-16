package example;

import io.flipt.api.FliptClient;
import io.flipt.api.evaluation.models.*;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    FliptClient fliptClient = FliptClient.builder().build();
    Map<String, String> context = new HashMap<>();

    context.put("fizz", "buzz");

    EvaluationRequest variantEvaluationRequest =
        EvaluationRequest.builder()
            .namespaceKey("default")
            .flagKey("flag1")
            .entityId("entity")
            .context(context)
            .build();

    EvaluationRequest booleanEvaluationRequest =
        EvaluationRequest.builder()
            .namespaceKey("default")
            .flagKey("flag_boolean")
            .entityId("entity")
            .context(context)
            .build();

    EvaluationRequest errorEvaluationRequest =
        EvaluationRequest.builder()
            .namespaceKey("default")
            .flagKey("flag1234")
            .entityId("entityId")
            .build();

    List<EvaluationRequest> evaluationRequests = new ArrayList<>();
    evaluationRequests.add(variantEvaluationRequest);
    evaluationRequests.add(booleanEvaluationRequest);
    evaluationRequests.add(errorEvaluationRequest);

    VariantEvaluationResponse variantEvaluationResponse =
        fliptClient.evaluation().variantEvaluation(variantEvaluationRequest);

    BooleanEvaluationResponse booleanEvaluationResponse =
        fliptClient.evaluation().booleanEvaluation(booleanEvaluationRequest);

    BatchEvaluationResponse batchEvaluationResponse =
        fliptClient
            .evaluation()
            .batchEvaluation(BatchEvaluationRequest.builder().requests(evaluationRequests).build());
  }
}
