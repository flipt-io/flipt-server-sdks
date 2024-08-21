from datetime import datetime
import requests


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
    def __init__(self,
                 token: str,
                 service_account_token_path:str = "/var/run/secrets/kubernetes.io/serviceaccount/token") -> None:
        self.token = token
        self.service_account_token_path = service_account_token_path
        self.token_expiry = None


    def authenticate(self, headers: dict[str, str]) -> None:
        # Check if token is available and not expired.
        if self.token and self.token_expiry and self.token_expiry > datetime.now().timestamp():
            headers["Authorization"] = f"Bearer {self.token}"

        # Read service account info from the local file system.
        try:
            with open(self.service_account_token_path, 'r') as token_file:
                service_account_token = token_file.read().strip()
        except Exception as e:
            raise RuntimeError(f"Failed to read service account token: {e}")

        # Send the token to flipt's auth endpoint and collect the response.
        payload = {"service_account_token": service_account_token}
        try:
            response = requests.post("http://flipt:8080/auth/v1/method/kubernetes/serviceaccount", json=payload)
        except requests.exceptions.RequestException as e:
            raise RuntimeError(f"Failed to authenticate with Flipt: {e}")

        # Parse the response, storing the token and expiration as a unix timestamp.
        try:
            response_data = response.json()
            self.token = response_data.get("clientToken")
            token_expiry_iso8601 = response_data.get("expiresAt")
            self.token_expiry = datetime.fromisoformat(token_expiry_iso8601).timestamp()
        except KeyError or ValueError as e:
            raise RuntimeError(f"Failed parsing authentication response: {e}")

        headers["Authorization"] = self.token
