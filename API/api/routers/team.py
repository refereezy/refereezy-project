from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Team
from schemas import TeamCreate, TeamResponse
from dependencies import get_db
from typing import List

router = APIRouter(prefix="/teams", tags=["Teams"])

# Get Pare
@router.get("/", response_model=List[TeamResponse])
def get_teams(db: Session = Depends(get_db)):
    return db.query(Team).all()

#GET Per ID
@router.get("/{team_id}", response_model=TeamResponse)
def get_team(team_id: int, db: Session = Depends(get_db)):
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="Team not found")
    return team

@router.get("/client/{client_id}", response_model=List[TeamResponse])
def get_teams_by_client(client_id: int, db: Session = Depends(get_db)):
    teams = db.query(Team).filter(Team.client_id == client_id).all()
    if not teams:
        raise HTTPException(status_code=404, detail="No teams found for the given client")
    return teams

# Post Team
@router.post("/", response_model=TeamResponse)
def create_team(team: TeamCreate, db: Session = Depends(get_db)):
    db_team = Team(**team.dict())
    db.add(db_team)
    db.commit()
    db.refresh(db_team)
    return db_team

@router.put("/{team_id}", response_model=TeamResponse)
def update_team(team_id: int, team: TeamCreate, db: Session = Depends(get_db)):
    db_team = db.query(Team).filter(Team.id == team_id).first()
    if not db_team:
        raise HTTPException(status_code=404, detail="Team not found")
    for key, value in team.dict().items():
        setattr(db_team, key, value)
    db.commit()
    db.refresh(db_team)
    return db_team

@router.delete("/{team_id}")
def delete_team(team_id: int, db: Session = Depends(get_db)):
    db_team = db.query(Team).filter(Team.id == team_id).first()
    if not db_team:
        raise HTTPException(status_code=404, detail="Team not found")
    db.delete(db_team)
    db.commit()
    return {"message": "Team deleted"}

