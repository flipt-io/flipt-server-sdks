package example;

import com.flipt.api.FliptClient;
import com.flipt.api.evaluation.models.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        FliptClient fliptClient = FliptClient.builder().build();
        Map<String, String> context = new HashMap<>();

        context.put("fizz", "buzz");

        EvaluationRequest variantEvaluationRequest = new EvaluationRequest("default", "flag1", "entity", context);
        EvaluationRequest booleanEvaluationRequest = new EvaluationRequest("default", "flag_boolean", "entity", context);
        EvaluationRequest errorEvaluationRequest = new EvaluationRequest("default", "flag1234", "entity", new HashMap<>());

        List<EvaluationRequest> evaluationRequests = new ArrayList<>();
        evaluationRequests.add(variantEvaluationRequest);
        evaluationRequests.add(booleanEvaluationRequest);
        evaluationRequests.add(errorEvaluationRequest);

        VariantEvaluationResponse variantEvaluationResponse = fliptClient.evaluation.variant(variantEvaluationRequest);
        BooleanEvaluationResponse booleanEvaluationResponse = fliptClient.evaluation.booleanEvaluation(booleanEvaluationRequest);

        BatchEvaluationResponse batchEvaluationResponse = fliptClient.evaluation.batch(new BatchEvaluationRequest(Optional.of(""), evaluationRequests));
    }
}
