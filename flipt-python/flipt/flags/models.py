from datetime import datetime
from enum import Enum
from typing import Any

from flipt.models import CamelAliasModel, PaginatedResponse


class FlagType(str, Enum):
    variant = "VARIANT_FLAG_TYPE"
    boolean = "BOOLEAN_FLAG_TYPE"


class Flag(CamelAliasModel):
    created_at: datetime
    description: str
    enabled: bool
    key: str
    name: str
    namespacekey: str | None = None
    type: FlagType
    updatedAt: datetime
    variants: list[Any]


class ListFlagsResponse(CamelAliasModel, PaginatedResponse):
    flags: list[Flag]
