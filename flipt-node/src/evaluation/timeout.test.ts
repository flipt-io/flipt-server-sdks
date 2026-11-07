import { Evaluation } from "./index";

describe("Evaluation timeout", () => {
  const timeout = jest.spyOn(AbortSignal, "timeout");
  const fetchMock = jest.spyOn(global, "fetch").mockResolvedValue({
    status: 200,
    json: async () => ({ enabled: true, reason: "MATCH_EVALUATION_REASON" })
  } as Response);

  afterEach(() => {
    timeout.mockClear();
    fetchMock.mockClear();
  });

  afterAll(() => {
    timeout.mockRestore();
    fetchMock.mockRestore();
  });

  it("rounds fractional seconds before creating the request signal", async () => {
    const evaluation = new Evaluation("http://localhost:8080", 1.001);

    await evaluation.boolean({
      namespaceKey: "default",
      flagKey: "flag",
      entityId: "entity",
      context: {}
    });

    expect(timeout).toHaveBeenCalledWith(1001);
    expect(fetchMock).toHaveBeenCalledWith(
      "http://localhost:8080/evaluate/v1/boolean",
      expect.objectContaining({ signal: expect.any(AbortSignal) })
    );
  });
});
