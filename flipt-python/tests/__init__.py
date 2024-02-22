import json
import os
import unittest
from flipt import FliptClient
from flipt.evaluation import (
    EvaluationRequest,
    BooleanEvaluationResponse,
    VariantEvaluationResponse,
)
from flipt.authentication import ClientTokenAuthentication


class TestFliptEvaluationClient(unittest.TestCase):
    def setUp(self) -> None:
        flipt_url = os.environ.get("FLIPT_URL")
        if flipt_url is None:
            raise Exception("FLIPT_URL not set")

        auth_token = os.environ.get("FLIPT_AUTH_TOKEN")
        if auth_token is None:
            raise Exception("FLIPT_AUTH_TOKEN not set")

        self.flipt_client = FliptClient(
            url=flipt_url, authentication=ClientTokenAuthentication(auth_token)
        )

        # tests.json should be injected into the container that these tests
        # are going to be run in.
        f = open("tests.json")
        self.data = json.load(f)
        f.close()

    def test_variant(self):
        variant_tests = self.data["VARIANT"]
        for test_scenario in variant_tests:
            if ("request" not in test_scenario) or ("expectation" not in test_scenario):
                raise Exception("malformed test")

            eval_request = EvaluationRequest.model_validate(test_scenario["request"])
            variant = self.flipt_client.evaluation.variant(eval_request)
            expectation = VariantEvaluationResponse.model_validate(
                test_scenario["expectation"]
            )
            self.assertEqual(expectation.flag_key, variant.flag_key)
            self.assertEqual(expectation.match, variant.match)
            self.assertEqual(expectation.variant_key, variant.variant_key)
            self.assertEqual(expectation.reason, variant.reason)
            for segment in variant.segment_keys:
                self.assertIn(segment, expectation.segment_keys)

    def test_boolean(self):
        boolean_tests = self.data["BOOLEAN"]
        for test_scenario in boolean_tests:
            if ("request" not in test_scenario) or ("expectation" not in test_scenario):
                raise Exception("malformed test")

            eval_request = EvaluationRequest.model_validate(test_scenario["request"])
            boolean = self.flipt_client.evaluation.boolean(eval_request)
            expectation = BooleanEvaluationResponse.model_validate(
                test_scenario["expectation"]
            )
            self.assertEqual(expectation.flag_key, boolean.flag_key)
            self.assertEqual(expectation.enabled, boolean.enabled)
            self.assertEqual(expectation.reason, boolean.reason)
