using Flipt.Clients;
using Flipt.Authentication;
using Flipt.DTOs;
using Flipt.Models;

namespace Flipt.Tests.Clients
{
    public class EvaluationTests : IAsyncLifetime
    {
        private string? _fliptURL;
        private string? _authToken;
        private Evaluation? _evaluation;


        /// <summary>
        /// This method initializes the test class.
        /// </summary>
        /// <returns></returns>
        /// <exception cref="InvalidOperationException"></exception>
        public async Task InitializeAsync()
        {
            _fliptURL = Environment.GetEnvironmentVariable("FLIPT_URL");
            _authToken = Environment.GetEnvironmentVariable("FLIPT_AUTH_TOKEN");

            if (string.IsNullOrEmpty(_fliptURL))
            {
                throw new InvalidOperationException("FLIPT_URL environment variable is not set.");
            }

            if (string.IsNullOrEmpty(_authToken))
            {
                throw new InvalidOperationException("FLIPT_AUTH_TOKEN environment variable is not set.");
            }

            var httpClient = new HttpClient();
            var headers = new Dictionary<string, string>
            {
                { "Accept", "application/json" }
            };

            _evaluation = Evaluation.Builder()
                .WithHttpClient(httpClient)
                .WithBaseUrl(_fliptURL)
                .WithAuthenticationStrategy(new ClientTokenAuthenticationStrategy(_authToken))
                .WithHeaders(headers)
                .Build();

            await Task.CompletedTask;
        }

        /// <summary>
        /// This method disposes the test class.
        /// </summary>
        /// <returns></returns>
        public Task DisposeAsync()
        {
            return Task.CompletedTask;
        }

        [Fact]
        public async Task EvaluateBooleanAsync_Should_ReturnValidResponse_When_ValidRequest()
        {
            // Arrange
            var context = new Dictionary<string, string>
            {
                { "fizz", "buzz" }
            };

            var request = new EvaluationRequest("default", "flag_boolean", "entity", context);

            // Act
            var response = await _evaluation?.EvaluateBooleanAsync(request)!;

            // Assert
            Assert.NotNull(response);
            Assert.True(response.Enabled);
            Assert.Equal("flag_boolean", response.FlagKey);
            Assert.Equal(Reason.MatchEvaluationReason, response.Reason);
            Assert.Equal("segment1", response.SegmentKeys[0]);
        }

        [Fact]
        public async Task EvaluateVariantAsync_Should_ReturnValidResponse_When_ValidRequest()
        {
            // Arrange
            var context = new Dictionary<string, string>
            {
                { "fizz", "buzz" }
            };

            var request = new EvaluationRequest("default", "flag1", "entity", context);

            // Act
            var response = await _evaluation?.EvaluateVariantAsync(request)!;

            // Assert
            Assert.NotNull(response);
            Assert.True(response.Match);
            Assert.Equal("flag1", response.FlagKey);
            Assert.Equal(Reason.MatchEvaluationReason, response.Reason);
            Assert.Equal("variant1", response.VariantKey);
            Assert.Equal("segment1", response.SegmentKeys[0]);
        }

        [Fact]
        public async Task EvaluateBatchAsync_Should_ReturnValidResponses_When_ValidRequests()
        {
            // Arrange
            var context = new Dictionary<string, string>
            {
                { "fizz", "buzz" }
            };

            var variantEvaluationRequest = new EvaluationRequest("default", "flag1", "entity", context);
            var booleanEvaluationRequest = new EvaluationRequest("default", "flag_boolean", "entity", context);
            var errorEvaluationRequest = new EvaluationRequest("default", "flag1234", "entity", []);

            var evaluationRequests = new List<EvaluationRequest>
            {
                variantEvaluationRequest,
                booleanEvaluationRequest,
                errorEvaluationRequest
            };

            var batchRequest = new BatchEvaluationRequest(evaluationRequests);

            // Act
            var batchResponse = await _evaluation?.EvaluateBatchAsync(batchRequest)!;

            // Assert
            Assert.NotNull(batchResponse);
            Assert.Equal(3, batchResponse.Responses.Length);

            // Variant
            var firstResponse = batchResponse.Responses[0];
            Assert.Equal(ResponseType.VariantEvaluationResponseType, firstResponse.Type);

            var variantResponse = firstResponse.VariantResponse;
            Assert.NotNull(variantResponse);
            Assert.True(variantResponse.Match);
            Assert.Equal("flag1", variantResponse.FlagKey);
            Assert.Equal(Reason.MatchEvaluationReason, variantResponse.Reason);
            Assert.Equal("variant1", variantResponse.VariantKey);
            Assert.Equal("segment1", variantResponse.SegmentKeys[0]);

            // Boolean
            var secondResponse = batchResponse.Responses[1];
            Assert.Equal(ResponseType.BooleanEvaluationResponseType, secondResponse.Type);

            var booleanResponse = secondResponse.BooleanResponse;
            Assert.NotNull(booleanResponse);
            Assert.True(booleanResponse.Enabled);
            Assert.Equal("flag_boolean", booleanResponse.FlagKey);
            Assert.Equal(Reason.MatchEvaluationReason, booleanResponse.Reason);
            Assert.Equal("segment1", booleanResponse.SegmentKeys[0]);

            // Error
            var thirdResponse = batchResponse.Responses[2];
            Assert.Equal(ResponseType.ErrorEvaluationResponseType, thirdResponse.Type);

            var errorResponse = thirdResponse.ErrorResponse;
            Assert.NotNull(errorResponse);
            Assert.Equal("flag1234", errorResponse.FlagKey);
            Assert.Equal("default", errorResponse.NamespaceKey);
            Assert.Equal("NOT_FOUND_ERROR_EVALUATION_REASON", errorResponse.Reason?.ToString());
        }
    }
}
