from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Player
from schemas import PlayerCreate, PlayerResponse
from dependencies import get_db
from typing import List

router = APIRouter(prefix="/players", tags=["Players"])

@router.get("/", response_model=List[PlayerResponse])
def get_players(db: Session = Depends(get_db)):
    return db.query(Player).all()

@router.get("/{player_id}", response_model=PlayerResponse)
def get_player(player_id: int, db: Session = Depends(get_db)):
    player = db.query(Player).filter(Player.id == player_id).first()
    if not player:
        raise HTTPException(status_code=404, detail="Player not found")
    return player

@router.post("/", response_model=PlayerResponse)
def create_player(player: PlayerCreate, db: Session = Depends(get_db)):
    db_player = Player(**player.dict())
    db.add(db_player)
    db.commit()
    db.refresh(db_player)
    return db_player

@router.put("/{player_id}", response_model=PlayerResponse)
def update_player(player_id: int, player: PlayerCreate, db: Session = Depends(get_db)):
    db_player = db.query(Player).filter(Player.id == player_id).first()
    if not db_player:
        raise HTTPException(status_code=404, detail="Player not found")
    for key, value in player.dict().items():
        setattr(db_player, key, value)
    db.commit()
    db.refresh(db_player)
    return db_player

@router.delete("/{player_id}")
def delete_player(player_id: int, db: Session = Depends(get_db)):
    db_player = db.query(Player).filter(Player.id == player_id).first()
    if not db_player:
        raise HTTPException(status_code=404, detail="Player not found")
    db.delete(db_player)
    db.commit()
    return {"message": "Player deleted"}
