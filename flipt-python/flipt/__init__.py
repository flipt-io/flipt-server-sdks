import typing
from .evaluation import Evaluation
from .authentication import AuthenticationStrategy


class FliptClient:
    def __init__(
        self,
        url: str = "http://localhost:8080",
        timeout: int = 60,
        authentication: typing.Optional[AuthenticationStrategy] = None,
    ):
        self.evaluation = Evaluation(url, timeout, authentication)
