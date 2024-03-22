from datetime import datetime
from enum import StrEnum

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
    created_at: datetime
    updated_at: datetime


class Flag(CamelAliasModel):
    key: str
    name: str
    description: str
    enabled: bool
    namespace_key: str
    type: FlagType
    created_at: datetime
    updated_at: datetime
    variants: list[Variant]


class ListFlagsResponse(PaginatedResponse):
    flags: list[Flag]
