from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Match, Team
from schemas import MatchCreate, MatchResponse
from dependencies import get_db
from typing import List

router = APIRouter(prefix="/matches", tags=["Matches"])

@router.get("/", response_model=List[MatchResponse])
def get_matches(db: Session = Depends(get_db)):
    return db.query(Match).all()

@router.get("/populated/{id}")
def get_populated_match(id: int, db: Session = Depends(get_db)):
    match = db.query(Match).filter(Match.id == id).first()
    
    if match is None:
        raise HTTPException(status_code=404, detail="Match not found")


    local: Team = match.local_team
    visitor: Team = match.visitor_team
    
    # Loads the team objects
    res = {
        "raw": {
            "id": match.id,
            "local_team_id": match.local_team_id,
            "visitor_team_id": match.visitor_team_id,
            "date": match.date,
            "referee_id": match.referee_id
        },
        "local_team": {
            **local.__dict__,
            "players": [
                {
                    "id": player.id,
                    "name": player.name,
                    "dorsal": player.dorsal_number
                    
                } for player in local.players
            ]
        },
        "visitor_team": {
            **visitor.__dict__,
            "players": [
                {
                    "id": player.id,
                    "name": player.name,
                    "dorsal": player.dorsal_number
                    
                } for player in visitor.players
            ]
        },
        "referee": match.referee
    }
    
    return res
    
@router.post("/", response_model=MatchResponse)
def create_match(match: MatchCreate, db: Session = Depends(get_db)):
    new_match = Match(**match.dict())
    db.add(new_match)
    db.commit()
    db.refresh(new_match)
    return new_match


@router.get("/{id}", response_model=MatchResponse)
def get_match(id: int, db: Session = Depends(get_db)):
    match = db.query(Match).filter(Match.id == id).first()
    if match is None:
        raise HTTPException(status_code=404, detail="Match not found")
    return match


@router.put("/{id}", response_model=MatchResponse)
def update_match(id: int, match: MatchCreate, db: Session = Depends(get_db)):
    existing_match = db.query(Match).filter(Match.id == id).first()
    if existing_match is None:
        raise HTTPException(status_code=404, detail="Match not found")
    for key, value in match.dict().items():
        setattr(existing_match, key, value)
    db.commit()
    db.refresh(existing_match)
    return existing_match


@router.delete("/{id}")
def delete_match(id: int, db: Session = Depends(get_db)):
    match = db.query(Match).filter(Match.id == id).first()
    if match is None:
        raise HTTPException(status_code=404, detail="Match not found")
    db.delete(match)
    db.commit()
    return {"detail": "Match deleted successfully"}
