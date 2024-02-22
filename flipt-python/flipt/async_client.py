import httpx

from .authentication import AuthenticationStrategy
from .evaluation import AsyncEvaluation


class AsyncFliptClient:
    def __init__(
        self,
        url: str = "http://localhost:8080",
        timeout: int = 60,
        authentication: AuthenticationStrategy | None = None,
    ):
        self.httpx_client = httpx.AsyncClient(timeout=timeout)

        self.evaluation = AsyncEvaluation(url, authentication, self.httpx_client)

    async def close(self) -> None:
        await self.httpx_client.aclose()
