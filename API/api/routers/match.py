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

@router.get("/client/{client_id}", response_model=List[MatchResponse])
def get_matches_by_client(client_id: int, db: Session = Depends(get_db)):
    matches = db.query(Match).filter(Match.client_id == client_id).all()
    if not matches:
        raise HTTPException(status_code=404, detail="No matches found for the given client ID")
    return matches



@router.get("/client/{id}/with_teams")
def get_client_matches_populated(client_id: int, db: Session = Depends(get_db)):
    matches = db.query(Match).filter(Match.client_id == client_id).all()
    
    if not matches:
        raise HTTPException(status_code=404, detail="No matches found for the given client ID")
    
    return [
        {
            **match.__dict__,
            "local_team": match.local_team,
            "visitor_team": match.visitor_team
        } for match in matches
    ]

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
            "referee_id": match.referee_id,
            "client_id": match.client_id
        },
        "local_team": {
            **local.__dict__,
            "players": [
                {
                    "id": player.id,
                    "name": player.name,
                    "dorsal": player.dorsal_number,
                    "is_goalkeeper": player.is_goalkeeper
                    
                } for player in local.players
            ]
        },
        "visitor_team": {
            **visitor.__dict__,
            "players": [
                {
                    "id": player.id,
                    "name": player.name,
                    "dorsal": player.dorsal_number,
                    "is_goalkeeper": player.is_goalkeeper
                    
                } for player in visitor.players
            ]
        },
        "referee": match.referee
    }
    
    return res

@router.get("/short/{id}")
def get_short_match(id: int, db: Session = Depends(get_db)):
    """Get a match with minimal information (for listings)"""
    match = db.query(Match).filter(Match.id == id).first()
    
    if match is None:
        raise HTTPException(status_code=404, detail="Match not found")

    local: Team = match.local_team
    visitor: Team = match.visitor_team
    
    # Return only basic information needed for listing
    res = {
        "raw": {
            "id": match.id,
            "local_team_id": match.local_team_id,
            "visitor_team_id": match.visitor_team_id,
            "date": match.date,
            "referee_id": match.referee_id,
            "client_id": match.client_id
        },
        "local_team": {
            "id": local.id,
            "name": local.name,
            "logo_url": local.logo_url
        },
        "visitor_team": {
            "id": visitor.id,
            "name": visitor.name,
            "logo_url": visitor.logo_url
        }
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
