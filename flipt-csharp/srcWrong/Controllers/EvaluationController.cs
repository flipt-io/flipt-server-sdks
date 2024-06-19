using System.Text.Json;
using Asp.Versioning;
using FliptCsharp.DTOs;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace FliptCsharp.Controllers;

[Route("api/evaluate/v{version:apiVersion}")]
[ApiVersion("1")]
[ApiController]
public class EvaluationController : ControllerBase
{
    private readonly HttpClient _client;
    private readonly string _version;
    public EvaluationController(IHttpClientFactory clientFactory)
    {
        _client = clientFactory.CreateClient("FliptApi");
        _version = HttpContext.GetRequestedApiVersion()?.ToString() ?? "1";
    }

    [HttpPost]
    [Route("boolean")]
    public async Task<ActionResult<BooleanEvaluationResponse>> BooleanEvaluation([FromBody] EvaluationRequest request)
        
    {
        // Get the API version as a string
        var url = $"evaluate/v{_version}/boolean"; 
        var response = await _client.PostAsJsonAsync(url, request);
        response.EnsureSuccessStatusCode();
        var jsonResponse = await response.Content.ReadAsStringAsync();
        var evaluationResponse = JsonSerializer.Deserialize<BooleanEvaluationResponse>(jsonResponse);
        if (evaluationResponse == null)
        {
            return BadRequest();
        }
        return evaluationResponse;
    }

    [HttpPost]
    [Route("boolean")]
    public async Task<ActionResult<VariantEvaluationResponse>> VariantEvaluation([FromBody] EvaluationRequest request)
        
    {
        // Get the API version as a string
        var url = $"evaluate/v{_version}/variant"; 
        var response = await _client.PostAsJsonAsync(url, request);
        response.EnsureSuccessStatusCode();
        var jsonResponse = await response.Content.ReadAsStringAsync();
        var evaluationResponse = JsonSerializer.Deserialize<VariantEvaluationResponse>(jsonResponse);
        if (evaluationResponse == null)
        {
            return BadRequest();
        }
        return evaluationResponse;
    }

    [HttpPost]
    [Route("batch")]
    public async Task<ActionResult<BatchEvaluationResponse>> BatchEvaluation([FromBody] BatchEvaluationRequest request)
        
    {
        // Get the API version as a string
        var url = $"evaluate/v{_version}/batch"; 
        var response = await _client.PostAsJsonAsync(url, request);
        response.EnsureSuccessStatusCode();
        var jsonResponse = await response.Content.ReadAsStringAsync();
        var evaluationResponse = JsonSerializer.Deserialize<BatchEvaluationResponse>(jsonResponse);
        if (evaluationResponse == null)
        {
            return BadRequest();
        }
        return evaluationResponse;
    }
}