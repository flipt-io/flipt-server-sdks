import { ClientTokenAuthentication } from "./index";
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

let client: FliptClient;

beforeEach(() => {
  client = new FliptClient({
    url: fliptUrl,
    authenticationStrategy: new ClientTokenAuthentication(authToken),
    headers: {
      "x-custom-header": "custom-value"
    }
  });
});

test("variant", async () => {
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

  if (!variant) {
    throw new Error("variant is undefined");
  }

  expect(variant.flagKey).toEqual("flag1");
  expect(variant.match).toEqual(true);
  expect(variant.reason).toEqual("MATCH_EVALUATION_REASON");
  expect(variant.variantKey).toEqual("variant1");
  expect(variant.segmentKeys).toContain("segment1");

  // Boolean
  expect(batch.responses[1].type).toEqual("BOOLEAN_EVALUATION_RESPONSE_TYPE");
  const boolean = batch.responses[1].booleanResponse;

  if (!boolean) {
    throw new Error("boolean is undefined");
  }

  expect(boolean.flagKey).toEqual("flag_boolean");
  expect(boolean.enabled).toEqual(true);
  expect(boolean.reason).toEqual("MATCH_EVALUATION_REASON");

  // Error
  expect(batch.responses[2].type).toEqual("ERROR_EVALUATION_RESPONSE_TYPE");
  const error = batch.responses[2].errorResponse;

  if (!error) {
    throw new Error("error is undefined");
  }

  expect(error.flagKey).toEqual("notfound");
  expect(error.namespaceKey).toEqual("default");
  expect(error.reason).toEqual("NOT_FOUND_ERROR_EVALUATION_REASON");
});
