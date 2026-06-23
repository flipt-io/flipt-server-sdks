from datetime import datetime
from enum import StrEnum

from pydantic import Field

from flipt.models import CamelAliasModel, PaginatedResponse


class FlagType(StrEnum):
    variant = "VARIANT_FLAG_TYPE"
    boolean = "BOOLEAN_FLAG_TYPE"


class Variant(CamelAliasModel):
    attachment: str
    description: str
    flag_key: str
    id: str
    key: str
    name: str
    namespace_key: str
    created_at: datetime | None = Field(
        default=None,
        description="Deprecated: populated in Flipt v1, will be None in Flipt v2",
    )
    updated_at: datetime | None = Field(
        default=None,
        description="Deprecated: populated in Flipt v1, will be None in Flipt v2",
    )


class Flag(CamelAliasModel):
    key: str
    name: str
    description: str
    enabled: bool
    namespace_key: str
    type: FlagType
    created_at: datetime | None = Field(
        default=None,
        description="Deprecated: populated in Flipt v1, will be None in Flipt v2",
    )
    updated_at: datetime | None = Field(
        default=None,
        description="Deprecated: populated in Flipt v1, will be None in Flipt v2",
    )
    variants: list[Variant]


class ListFlagsResponse(PaginatedResponse):
    flags: list[Flag]
