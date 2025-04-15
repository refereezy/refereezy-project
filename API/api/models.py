from sqlalchemy import Column, Integer, String, Date, DateTime, ForeignKey, Boolean
from sqlalchemy.orm import relationship
from database import Base

class Client(Base):
    __tablename__ = "client"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    plan = Column(String)
    plan_expiration = Column(Date)
    email = Column(String, unique=True)
    password = Column(String)
    phone = Column(String)

    teams = relationship("Team", back_populates="client", cascade="all, delete")
    players = relationship("Player", back_populates="client", cascade="all, delete")
    referees = relationship("Referee", back_populates="client", cascade="all, delete")
    match_groups = relationship("MatchGroup", back_populates="client", cascade="all, delete")
    matches = relationship("Match", back_populates="client", cascade="all, delete")

class Team(Base):
    __tablename__ = "team"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    logo_url = Column(String)
    primary_color = Column(String)
    secondary_color = Column(String)
    client_id = Column(Integer, ForeignKey("client.id"), nullable=False)
    
    client = relationship("Client", back_populates="teams")
    players = relationship("Player", back_populates="team", cascade="all, delete")

class Player(Base):
    __tablename__ = "player"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    dorsal_number = Column(Integer, nullable=False)
    dni = Column(String, unique=True, nullable=False)
    team_id = Column(Integer, ForeignKey("team.id"))
    client_id = Column(Integer, ForeignKey("client.id"), nullable=False)
    is_goalkeeper = Column(Boolean, nullable=False)

    team = relationship("Team", foreign_keys=[team_id])
    client = relationship("Client", foreign_keys=[client_id])

class Referee(Base):
    __tablename__ = "referee"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    dni = Column(String, unique=True, nullable=False)
    clock_code = Column(String, ForeignKey("clock.code"))
    password = Column(String, nullable=False)
    client_id = Column(Integer, ForeignKey("client.id"), nullable=False)

    client = relationship("Client", back_populates="referees")
    matches = relationship("Match", back_populates="referee", cascade="all, delete")

class MatchGroup(Base):
    __tablename__ = "match_group"

    id = Column(Integer, primary_key=True, index=True)
    visibility = Column(String, nullable=False)
    code = Column(String, unique=True, nullable=False)
    client_id = Column(Integer, ForeignKey("client.id"), nullable=False)

    client = relationship("Client", back_populates="match_groups")
    matches = relationship("Match", back_populates="match_group", cascade="all, delete")

class Match(Base):
    __tablename__ = "matches"

    id = Column(Integer, primary_key=True, index=True)
    date = Column(DateTime, nullable=False)
    matchgroup_id = Column(Integer, ForeignKey("match_group.id"))
    client_id = Column(Integer, ForeignKey("client.id"), nullable=False)
    referee_id = Column(Integer, ForeignKey("referee.id"), nullable=True)
    local_team_id = Column(Integer, ForeignKey("team.id"), nullable=False)
    visitor_team_id = Column(Integer, ForeignKey("team.id"), nullable=False)

    match_group = relationship("MatchGroup", back_populates="matches")
    client = relationship("Client", back_populates="matches")
    referee = relationship("Referee", foreign_keys=[referee_id])
    local_team = relationship("Team", foreign_keys=[local_team_id])
    visitor_team = relationship("Team", foreign_keys=[visitor_team_id])



class Clock(Base):
    __tablename__ = "clock"

    code = Column(String, primary_key=True)
    
