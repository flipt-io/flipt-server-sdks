using Flipt.Authentication;
using Flipt.DTOs;
using Flipt.Utilities;

namespace Flipt.Example;

public class Program
{
    public static async Task Main()
    {
        var fliptClient = FliptClient.Builder()
            .WithUrl("http://localhost:8080")
            .WithAuthentication(new ClientTokenAuthenticationStrategy("Client-Token"))
            .WithTimeout(30)
            .Build();

        Dictionary<string, string> context = new() { { "fizz", "buzz" } };

        var evaluation = fliptClient.Evaluation;

        var variantEvaluation = new EvaluationRequest("default", "flag1", "entity", context);
        var variantEvaluationResponse = await evaluation.EvaluateVariantAsync(variantEvaluation);

        var boolEvaluation = new EvaluationRequest("default", "bool_flag", "entity", context);
        var boolEvaluationResponse = await evaluation.EvaluateBooleanAsync(boolEvaluation);

        var list = new List<EvaluationRequest>
        {
            variantEvaluation,
            boolEvaluation
        };
        var batchEvaluationRequest = new BatchEvaluationRequest(list);
        var batchEvaluationResponse = await evaluation.EvaluateBatchAsync(batchEvaluationRequest);
    }
}