import { FliptClient } from "../src";

const fliptClient = new FliptClient();

async function example() {
  const variantEvaluationResponse = await fliptClient.evaluation.variant({
    namespace_key: "default",
    flag_key: "flagll",
    entity_id: "entity",
    context: { fizz: "buzz" }
  });

  const booleanEvaluationResponse = await fliptClient.evaluation.boolean({
    namespace_key: "default",
    flag_key: "flag_boolean",
    entity_id: "entity",
    context: { fizz: "buzz" }
  });

  const batchEvaluationResponse = await fliptClient.evaluation.batch({
    requests: [
      {
        namespace_key: "default",
        flag_key: "flag1",
        entity_id: "entity",
        context: { fizz: "buzz" }
      },
      {
        namespace_key: "default",
        flag_key: "flag_boolean",
        entity_id: "entity",
        context: { fizz: "buzz" }
      }
    ]
  });

  console.log(variantEvaluationResponse);
  console.log(booleanEvaluationResponse);
  console.log(batchEvaluationResponse);
}

example().then(() => console.log("done"));
