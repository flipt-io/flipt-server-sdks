import typing
from .evaluation import Evaluation


class FliptClient:
    def __init__(
        self,
        url: str = "http://localhost:8080",
        client_token: typing.Optional[str] = None,
        jwt_token: typing.Optional[str] = None,
        timeout: int = 60,
    ):
        self.evaluation = Evaluation(url, client_token, jwt_token, timeout)
