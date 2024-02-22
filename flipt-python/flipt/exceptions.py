class BaseFliptError(Exception):
    pass


class FliptApiError(BaseFliptError):
    message: str
    status_code: int

    def __init__(self, message: str, status_code: int):
        self.message = message
        self.status_code = status_code

    def __repr__(self) -> str:
        return f"<FliptApiError(message={self.message}, status_code={self.status_code})>"
