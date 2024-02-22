import { ClientTokenAuthentication } from "./index";
import { FliptClient } from ".";
import fs from "fs";

const fileData = fs.readFileSync("tests.json", "utf-8");
const data = JSON.parse(fileData);

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
  const client = new FliptClient({
    url: fliptUrl,
    authenticationStrategy: new ClientTokenAuthentication(authToken)
  });

  const variantTests = data["VARIANT"];

  for (const testScenario of variantTests) {
    if (
      testScenario["request"] === undefined ||
      testScenario["expectation"] === undefined
    ) {
      throw new Error("malformed test");
    }

    const variant = await client.evaluation.variant(testScenario["request"]);

    const expectation = testScenario["expectation"];

    expect(variant.flagKey).toEqual(expectation.flagKey);
    expect(variant.match).toEqual(expectation.match);
    expect(variant.reason).toEqual(expectation.reason);
    expect(variant.variantKey).toEqual(expectation.variantKey);
    for (const segment of variant.segmentKeys) {
      expect(expectation.segmentKeys).toContain(segment);
    }
  }
});

test("boolean", async () => {
  const client = new FliptClient({
    url: fliptUrl,
    authenticationStrategy: new ClientTokenAuthentication(authToken)
  });

  const booleanTests = data["BOOLEAN"];

  for (const testScenario of booleanTests) {
    const request = testScenario["request"];
    const expectation = testScenario["expectation"];
    if (request === undefined || expectation === undefined) {
      throw new Error("malformed test");
    }

    const boolean = await client.evaluation.boolean(request);

    expect(boolean.flagKey).toEqual(expectation.flagKey);
    expect(boolean.enabled).toEqual(expectation.enabled);
    expect(boolean.reason).toEqual(expectation.reason);
  }
});
