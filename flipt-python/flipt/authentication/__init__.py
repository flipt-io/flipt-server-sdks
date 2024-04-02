import base64


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


class BasicAuthentication(AuthenticationStrategy):
    def __init__(self, username: str, password: str) -> None:
        self.username = username
        self.password = password

    def authenticate(self, headers: dict[str, str]) -> None:
        b64_creds = base64.b64encode(f"{self.username}:{self.password}".encode("utf-8")).decode("utf-8")
        headers["Authorization"] = f"Basic {b64_creds}')"
