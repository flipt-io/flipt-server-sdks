import { FliptClient } from ".";

const fliptUrl = process.env["FLIPT_URL"];
if (!fliptUrl) {
  console.error("please set the FLIPT_URL environment variable");
  process.exit(1);
}

const authToken = process.env["FLIPT_AUTH_TOKEN"];
if (!authToken) {
  console.error("please set the FLIPT_AUTH_TOKEN environment variable");
  process.exit(1);
}

test("variant", async () => {
  const client = new FliptClient({ url: fliptUrl, clientToken: authToken });

  const variant = await client.evaluation.variant({
    namespaceKey: "default",
    flagKey: "flag1",
    entityId: "entity",
    context: { fizz: "buzz" }
  });

  expect(variant.flagKey).toEqual("flag1");
  expect(variant.match).toEqual(true);
  expect(variant.reason).toEqual("MATCH_EVALUATION_REASON");
  expect(variant.variantKey).toEqual("variant1");
  expect(variant.segmentKeys).toContain("segment1");
});

test("boolean", async () => {
  const client = new FliptClient({ url: fliptUrl, clientToken: authToken });

  const boolean = await client.evaluation.boolean({
    namespaceKey: "default",
    flagKey: "flag_boolean",
    entityId: "entity",
    context: { fizz: "buzz" }
  });

  expect(boolean.flagKey).toEqual("flag_boolean");
  expect(boolean.enabled).toEqual(true);
  expect(boolean.reason).toEqual("MATCH_EVALUATION_REASON");
});

test("batch", async () => {
  const client = new FliptClient({ url: fliptUrl, clientToken: authToken });
  const batch = await client.evaluation.batch({
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
      },
      {
        namespaceKey: "default",
        flagKey: "notfound",
        entityId: "entity",
        context: {}
      }
    ]
  });

  expect(batch.responses).toHaveLength(3);

  // Variant
  expect(batch.responses[0].type).toEqual("VARIANT_EVALUATION_RESPONSE_TYPE");
  const variant = batch.responses[0].variantResponse;
  expect(variant.flagKey).toEqual("flag1");
  expect(variant.match).toEqual(true);
  expect(variant.reason).toEqual("MATCH_EVALUATION_REASON");
  expect(variant.variantKey).toEqual("variant1");
  expect(variant.segmentKeys).toContain("segment1");

  // Boolean
  expect(batch.responses[1].type).toEqual("BOOLEAN_EVALUATION_RESPONSE_TYPE");
  const boolean = batch.responses[1].booleanResponse;
  expect(boolean.flagKey).toEqual("flag_boolean");
  expect(boolean.enabled).toEqual(true);
  expect(boolean.reason).toEqual("MATCH_EVALUATION_REASON");

  // Error
  expect(batch.responses[2].type).toEqual("ERROR_EVALUATION_RESPONSE_TYPE");
  const error = batch.responses[2].errorResponse;
  expect(error.flagKey).toEqual("notfound");
  expect(error.namespaceKey).toEqual("default");
  expect(error.reason).toEqual("NOT_FOUND_ERROR_EVALUATION_REASON");
});
