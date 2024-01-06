import os
import unittest
from flipt import FliptClient
from flipt.evaluation import BatchEvaluationRequest, EvaluationRequest


class TestFliptEvaluationClient(unittest.TestCase):
    def setUp(self) -> None:
        flipt_url = os.environ.get("FLIPT_URL")
        if flipt_url is None:
            raise Exception("FLIPT_URL not set")

        auth_token = os.environ.get("FLIPT_AUTH_TOKEN")
        if auth_token is None:
            raise Exception("FLIPT_AUTH_TOKEN not set")

        self.flipt_client = FliptClient(url=flipt_url, token=auth_token)

    def test_variant(self):
        variant = self.flipt_client.evaluation.variant(
            EvaluationRequest(
                namespace_key="default",
                flag_key="flag1",
                entity_id="entity",
                context={"fizz": "buzz"},
            )
        )
        self.assertTrue(variant.match)
        self.assertEqual("flag1", variant.flag_key)
        self.assertEqual("variant1", variant.variant_key)
        self.assertEqual("MATCH_EVALUATION_REASON", variant.reason)
        self.assertIn("segment1", variant.segment_keys)

    def test_boolean(self):
        boolean = self.flipt_client.evaluation.boolean(
            EvaluationRequest(
                namespace_key="default",
                flag_key="flag_boolean",
                entity_id="entity",
                context={"fizz": "buzz"},
            )
        )
        self.assertTrue(boolean.enabled)
        self.assertEqual("flag_boolean", boolean.flag_key)
        self.assertEqual("MATCH_EVALUATION_REASON", boolean.reason)

    def test_batch(self):
        batch = self.flipt_client.evaluation.batch(
            BatchEvaluationRequest(
                requests=[
                    EvaluationRequest(
                        namespace_key="default",
                        flag_key="flag1",
                        entity_id="entity",
                        context={"fizz": "buzz"},
                    ),
                    EvaluationRequest(
                        namespace_key="default",
                        flag_key="flag_boolean",
                        entity_id="entity",
                        context={"fizz": "buzz"},
                    ),
                    EvaluationRequest(
                        namespace_key="default",
                        flag_key="notfound",
                        entity_id="entity",
                        context={"fizz": "buzz"},
                    ),
                ]
            )
        )

        self.assertEqual(3, len(batch.responses))

        # Variant
        self.assertEqual("VARIANT_EVALUATION_RESPONSE_TYPE", batch.responses[0].type)
        variant = batch.responses[0].variant_response
        self.assertTrue(variant.match)
        self.assertEqual("flag1", variant.flag_key)
        self.assertEqual("variant1", variant.variant_key)
        self.assertEqual("MATCH_EVALUATION_REASON", variant.reason)
        self.assertIn("segment1", variant.segment_keys)

        # Boolean
        self.assertEqual("BOOLEAN_EVALUATION_RESPONSE_TYPE", batch.responses[1].type)
        boolean = batch.responses[1].boolean_response
        self.assertTrue(boolean.enabled)
        self.assertEqual("flag_boolean", boolean.flag_key)
        self.assertEqual("MATCH_EVALUATION_REASON", boolean.reason)

        # Error
        self.assertEqual("ERROR_EVALUATION_RESPONSE_TYPE", batch.responses[2].type)
        error = batch.responses[2].error_response
        self.assertEqual("notfound", error.flag_key)
        self.assertEqual("default", error.namespace_key)
        self.assertEqual("NOT_FOUND_ERROR_EVALUATION_REASON", error.reason)
