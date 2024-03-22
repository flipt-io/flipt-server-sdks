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
