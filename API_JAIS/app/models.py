from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from database import Base

class Team(Base):
    __tablename__ = "teams"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, unique=True, index=True)
    country = Column(String)
    primary_Color = Column(String)
    secondary_Color = Column(String)
    logo = Column(String)

    players = relationship("Player", back_populates="team")  

class Player(Base):
    __tablename__ = "players"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    jersey_number = Column(Integer)
    dni = Column(String, unique=True)
    
    team_id = Column(Integer, ForeignKey("teams.id"))
    team = relationship("Team", back_populates="players")  

    client_id = Column(Integer, ForeignKey("clients.id"))
    client = relationship("Client", back_populates="players")  

class Referee(Base):
    __tablename__ = "referees"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    dni = Column(String, unique=True)

    client_id = Column(Integer, ForeignKey("clients.id"))
    client = relationship("Client", back_populates="referee")  

class MatchGroup(Base):
    __tablename__ = "match_groups"

    id = Column(Integer, primary_key=True, index=True)
    visibility = Column(String)
    name = Column(String)
    code = Column(String, unique=True)

    client_id = Column(Integer, ForeignKey("clients.id"))
    client = relationship("Client", back_populates="matchGroup") 

    matches = relationship("Match", back_populates="group")  

class Match(Base):
    __tablename__ = "matches"

    id = Column(Integer, primary_key=True, index=True)
    date = Column(DateTime)
    
    home_team_id = Column(Integer, ForeignKey("teams.id"))
    away_team_id = Column(Integer, ForeignKey("teams.id"))
    referee_id = Column(Integer, ForeignKey("referees.id"))
    group_id = Column(Integer, ForeignKey("match_groups.id"))

    home_team = relationship("Team", foreign_keys=[home_team_id])
    away_team = relationship("Team", foreign_keys=[away_team_id])
    referee = relationship("Referee")
    group = relationship("MatchGroup", back_populates="matches")  

class Client(Base):
    __tablename__ = "clients"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    plan = Column(String)
    plan_expiration = Column(DateTime)
    email = Column(String, unique=True)
    phone = Column(String)

    players = relationship("Player", back_populates="client")  
    referees = relationship("Referee", back_populates="client")  
    match_groups = relationship("MatchGroup", back_populates="client") 
