class AuthenticationStrategy:
    def authenticate(self, headers: dict):
        raise NotImplementedError()


class ClientTokenAuthentication(AuthenticationStrategy):
    def __init__(self, token: str):
        self.token = token

    def authenticate(self, headers: dict):
        headers["Authorization"] = f"Bearer {self.token}"


class JWTAuthentication(AuthenticationStrategy):
    def __init__(self, token: str):
        self.token = token

    def authenticate(self, headers: dict):
        headers["Authorization"] = f"JWT {self.token}"
