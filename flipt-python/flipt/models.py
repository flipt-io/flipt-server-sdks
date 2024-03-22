from pydantic import AliasGenerator, BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class CamelAliasModel(BaseModel):
    model_config = ConfigDict(
        alias_generator=AliasGenerator(alias=to_camel),
        populate_by_name=True,
    )


class CommonParameters(CamelAliasModel):
    reference: str | None = None


class ListParameters(CommonParameters):
    limit: int | None = None
    offset: int | None = None
    page_token: str | None = None


class PaginatedResponse(CamelAliasModel):
    next_page_token: str
    total_count: int
