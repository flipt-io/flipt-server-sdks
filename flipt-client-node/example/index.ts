import { FliptClient } from "../src";

const fliptClient = new FliptClient();

async function example() {
  const variantEvaluationResponse = await fliptClient.evaluation.variant({
    namespaceKey: "default",
    flagKey: "flag1",
    entityId: "entity",
    context: { fizz: "buzz" }
  });

  const booleanEvaluationResponse = await fliptClient.evaluation.boolean({
    namespaceKey: "default",
    flagKey: "flag_boolean",
    entityId: "entity",
    context: { fizz: "buzz" }
  });

  const batchEvaluationResponse = await fliptClient.evaluation.batch({
    requests: [
      {
        namespaceKey: "default",
        flagKey: "flag1",
        entityId: "entity",
        context: { fizz: "buzz" }
      },
      {
        namespaceKey: "default",
        flagKey: "flag_boolean",
        entityId: "entity",
        context: { fizz: "buzz" }
      }
    ]
  });

  console.log(variantEvaluationResponse);
  console.log(booleanEvaluationResponse);
  console.log(batchEvaluationResponse);
}

example().then(() => console.log("done"));
