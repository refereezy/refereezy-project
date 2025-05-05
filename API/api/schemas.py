from datetime import datetime
from pydantic import BaseModel
from typing import Optional
from fastapi import Form

#  Model para Team
class TeamBase(BaseModel):
    name: str
    primary_color: str
    secondary_color: str
    logo_url: str
    client_id: int

class TeamCreate(BaseModel):
    name: str
    primary_color: str
    secondary_color: str
    client_id: int

    @classmethod
    def as_form(
        cls,
        name: str = Form(...),
        primary_color: str = Form(...),
        secondary_color: str = Form(...),
        client_id: int = Form(...)
    ):
        return cls(
            name=name,
            primary_color=primary_color,
            secondary_color=secondary_color,
            client_id=client_id
        )

class TeamResponse(TeamBase):
    id: int
    class Config:
        orm_mode = True

#  Model para Player
class PlayerBase(BaseModel):
    # id: int
    name: str
    dorsal_number: int
    dni: str
    team_id: int
    client_id: int
    is_goalkeeper: bool

class PlayerCreate(PlayerBase):
    pass

class PlayerResponse(PlayerBase):
    id: int
    class Config:
        orm_mode = True

#  Model para Match
class MatchBase(BaseModel):
    # id: int
    date: datetime
    matchgroup_id: Optional[int]
    client_id: int
    referee_id: Optional[int]
    local_team_id: int
    visitor_team_id: int

class MatchCreate(MatchBase):
    pass

class MatchResponse(MatchBase):
    id: int
    class Config:
        orm_mode = True

#  Model para Referee
class RefereeBase(BaseModel): # base
    # id: int
    name: str
    dni: str
    client_id: int
    clock_code: Optional[str]
    
class RefereeLoad(BaseModel): # para login automatico con token
    id: int
    token: str
    
class RefereeLogin(BaseModel): # para login con credenciales
    dni: str
    password: str

class RefereeCreate(RefereeBase): # para crear, requiere contraseña
    password: str

class RefereeUpdate(BaseModel): # para actualizar, requiere token
    token: str
    new_password: Optional[str]

class RefereeResponse(RefereeBase): # para mostrar al publico
    id: int
    class Config:
        orm_mode = True

class RefereeData(RefereeResponse): # respuesta de login
    token: str

#  Model para MatchGroup
class MatchGroupBase(BaseModel):
    # id: int
    name: str
    visibility: str
    code: str
    client_id: int

class MatchGroupCreate(MatchGroupBase):
    pass

class MatchGroupResponse(MatchGroupBase):
    id: int
    class Config:
        orm_mode = True

#  Model para Client
class ClientBase(BaseModel):
    # id: int
    name: str
    email: str
    password: str # no estaba
    phone: str

class ClientCreate(ClientBase):
    pass

class ClientResponse(ClientBase):
    plan: str
    plan_expiration: datetime
    id: int
    class Config:
        orm_mode = True
        
class ClockSchema(BaseModel):
    code: str


# Class for Login
class ClientLogin(BaseModel):
    email: str
    password: str