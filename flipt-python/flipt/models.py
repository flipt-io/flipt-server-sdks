from pydantic import AliasGenerator, BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class CamelAliasModel(BaseModel):
    model_config = ConfigDict(
        alias_generator=AliasGenerator(alias=to_camel),
        populate_by_name=True,
    )


class ListParameters(BaseModel):
    limit: int | None = None
    offset: int | None = None
    pageToken: str | None = None
    reference: str | None = None


class PaginatedResponse(BaseModel):
    nextPageToken: str
    totalCount: int
