import { Flags } from "./index";

describe("Flags timeout", () => {
  const timeout = jest.spyOn(AbortSignal, "timeout");
  const fetchMock = jest.spyOn(global, "fetch").mockResolvedValue({
    status: 200,
    json: async () => ({})
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
    const flags = new Flags("http://localhost:8080", 1.001);

    await flags.listFlags("default");

    expect(timeout).toHaveBeenCalledWith(1001);
    expect(fetchMock).toHaveBeenCalledWith(
      "http://localhost:8080/api/v1/namespaces/default/flags",
      expect.objectContaining({ signal: expect.any(AbortSignal) })
    );
  });
});
