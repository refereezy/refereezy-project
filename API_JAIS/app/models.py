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
    logo = Column(String)
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

    team = relationship("Team", back_populates="players")
    client = relationship("Client", back_populates="players")

class Referee(Base):
    __tablename__ = "referee"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    dni = Column(String, unique=True, nullable=False)
    client_id = Column(Integer, ForeignKey("client.id"), nullable=False)

    client = relationship("Client", back_populates="referees")
    matches = relationship("MatchReferee", back_populates="referee", cascade="all, delete")

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
    local_team_id = Column(Integer, ForeignKey("team.id"), nullable=False)
    visitor_team_id = Column(Integer, ForeignKey("team.id"), nullable=False)

    match_group = relationship("MatchGroup", back_populates="matches")
    client = relationship("Client", back_populates="matches")
    local_team = relationship("Team", foreign_keys=[local_team_id])
    visitor_team = relationship("Team", foreign_keys=[visitor_team_id])
    referees = relationship("MatchReferee", back_populates="match", cascade="all, delete")

class MatchReferee(Base):
    __tablename__ = "match_referees"

    match_id = Column(Integer, ForeignKey("matches.id"), primary_key=True)
    referee_id = Column(Integer, ForeignKey("referee.id"), primary_key=True)

    match = relationship("Match", back_populates="referees")
    referee = relationship("Referee", back_populates="matches")

class Clock(Base):
    __tablename__ = "clock"

    code = Column(String, primary_key=True)

class Temp2FA(Base):
    __tablename__ = "temp_2fa"

    referee_id = Column(Integer, ForeignKey("referee.id", ondelete="CASCADE"), primary_key=True)
    twofa_code = Column(String(2))
    clock_code = Column(String, ForeignKey("clock.code", ondelete="CASCADE"), nullable=False)
    expiration = Column(DateTime, nullable=False)
    paired = Column(Boolean, nullable=False)

    referee = relationship("Referee")
    
