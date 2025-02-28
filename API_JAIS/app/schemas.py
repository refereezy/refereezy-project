from pydantic import BaseModel
from typing import List, Optional

#  Model para Team
class TeamBase(BaseModel):
    name: str
    country: str
    primary_Color: str
    secondary_Color: str
    logo: str
    client_id: int

class TeamCreate(TeamBase):
    pass

class TeamResponse(TeamBase):
    id: int
    class Config:
        orm_mode = True

#  Model para Player
class PlayerBase(BaseModel):
    name: str
    jersey_number: int
    dni: str
    team_id: int
    client_id: int

class PlayerCreate(PlayerBase):
    pass

class PlayerResponse(PlayerBase):
    id: int
    class Config:
        orm_mode = True

#  Model para Match
class MatchBase(BaseModel):
    name: str
    country: str
    primary_Color: str
    secondary_Color: str
    logo: str
    client_id: int

class MatchCreate(MatchBase):
    pass

class MatchResponse(MatchBase):
    id: int
    class Config:
        orm_mode = True

#  Model para Referee
class RefereeBase(BaseModel):
    name: str
    country: str
    primary_Color: str
    secondary_Color: str
    logo: str
    client_id: int

class RefereeCreate(RefereeBase):
    pass

class RefereeResponse(RefereeBase):
    id: int
    class Config:
        orm_mode = True

#  Model para MatchGroup
class MatchGroupBase(BaseModel):
    name: str
    country: str
    primary_Color: str
    secondary_Color: str
    logo: str
    client_id: int

class MatchGroupCreate(MatchGroupBase):
    pass

class MatchGroupResponse(MatchGroupBase):
    id: int
    class Config:
        orm_mode = True

#  Model para Client
class ClientBase(BaseModel):
    name: str
    country: str
    primary_Color: str
    secondary_Color: str
    logo: str
    client_id: int

class ClientCreate(ClientBase):
    pass

class ClientResponse(ClientBase):
    id: int
    class Config:
        orm_mode = True