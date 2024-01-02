from .evaluation import Evaluation


class FliptClient:
    def __init__(
        self, url: str = "http://localhost:8080", token: str = "", timeout: int = 10
    ):
        self.evaluation = Evaluation(url, token, timeout)
