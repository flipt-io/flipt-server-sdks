import requests
from pathlib import Path

from datetime import datetime



class AuthenticationStrategy:
    def authenticate(self, headers: dict[str, str]) -> None:
        raise NotImplementedError


class ClientTokenAuthentication(AuthenticationStrategy):
    def __init__(self, token: str) -> None:
        self.token = token

    def authenticate(self, headers: dict[str, str]) -> None:
        headers["Authorization"] = f"Bearer {self.token}"


class JWTAuthentication(AuthenticationStrategy):
    def __init__(self, token: str) -> None:
        self.token = token

    def authenticate(self, headers: dict[str, str]) -> None:
        headers["Authorization"] = f"JWT {self.token}"


class KubernetesAuthentication(AuthenticationStrategy):
    default_service_token_path = "/var/run/secrets/kubernetes.io/serviceaccount/token"  # noqa: S105
    def __init__(self,
                 token: str,
                 service_account_token_path:str = default_service_token_path) -> None:
        self.token = token
        self.service_account_token_path = service_account_token_path
        self.token_expiry = None

    def authenticate(self, headers: dict[str, str]) -> None:
        # Check if token is available and not expired.
        if self.token and self.token_expiry and self.token_expiry > datetime.now(tz=datetime.timezone.utc).timestamp():
            headers["Authorization"] = f"Bearer {self.token}"
            return

        # Read service account info from the local file system.
        try:
            with Path.open(Path(self.service_account_token_path), "r") as token_file:
                service_account_token = token_file.read().strip()
        except IOError as e:
            raise RuntimeError("Failed to read service account token.") from e

        # Send the token to flipt"s auth endpoint and collect the response.
        payload = {"service_account_token": service_account_token}
        try:
            response = requests.post("http://flipt:8080/auth/v1/method/kubernetes/serviceaccount",
                                     json=payload,
                                     timeout=5)
        except requests.exceptions.RequestException as e:
            raise RuntimeError("Failed to authenticate with Flipt.") from e

        # Parse the response, storing the token and expiration as a unix timestamp.
        try:
            response_data = response.json()
            self.token = response_data.get("clientToken")
            self.token_expiry = response_data.get("expiresAt")
        except (KeyError, ValueError) as e:
            raise RuntimeError("Failed parsing authentication response.") from e

        headers["Authorization"] = f"Bearer {self.token}"
