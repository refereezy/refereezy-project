from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Referee
from schemas import RefereeCreate, RefereeResponse, RefereeUpdate, RefereeLogin, RefereeLoad
from dependencies import get_db
from typing import List
from datetime import datetime, timedelta

router = APIRouter(prefix="/referee", tags=["Referees"])

@router.get("/", response_model=List[RefereeResponse])
def get_referees(db: Session = Depends(get_db)):
    return db.query(Referee).all()

@router.get("/{referee_id}", response_model=RefereeResponse)
def get_referee(referee_id: int, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.id == referee_id).first()
    if not referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    return referee

@router.get("/client/{client_id}", response_model=List[RefereeResponse])
def get_referees_by_client(client_id: int, db: Session = Depends(get_db)):
    referees = db.query(Referee).filter(Referee.client_id == client_id).all()
    if not referees:
        raise HTTPException(status_code=404, detail="No referees found for the given client ID")
    return referees

@router.get("/load/{referee_id}/{password}", response_model=RefereeLoad)
def map_referee(referee_id: int, password: str, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.id == referee_id).first()
    
    if not referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    
    if referee.password != password:
        raise HTTPException(status_code=401, detail="Unauthorized: Password does not match")
        
    return referee

@router.post("/", response_model=RefereeResponse)
def create_referee(referee: RefereeCreate, db: Session = Depends(get_db)):
    db_referee = Referee(**referee.dict())
    db.add(db_referee)
    db.commit()
    db.refresh(db_referee)
    return db_referee

@router.put("/{referee_id}", response_model=RefereeResponse)
def update_referee(referee_id: int, referee: RefereeCreate, db: Session = Depends(get_db)):
    db_referee = db.query(Referee).filter(Referee.id == referee_id).first()
    if not db_referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    for key, value in referee.dict().items():
        setattr(db_referee, key, value)
    db.commit()
    db.refresh(db_referee)
    return db_referee

@router.delete("/{referee_id}")
def delete_referee(referee_id: int, db: Session = Depends(get_db)):
    db_referee = db.query(Referee).filter(Referee.id == referee_id).first()
    if not db_referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    db.delete(db_referee)
    db.commit()
    return {"message": "Referee deleted"}


@router.get("/{referee_id}/matches")
def get_referee_matches(referee_id: int, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.id == referee_id).first()
    if referee:
        today = datetime.now().date()
        referee.matches = [match for match in referee.matches if match.date.date() >= today]
    if not referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    return referee.matches

@router.post("/login", response_model=RefereeLoad)
def login_referee(referee: RefereeLogin, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.dni == referee.dni, Referee.password == referee.password).first()
    if not referee:
        raise HTTPException(status_code=401, detail="Invalid DNI or password")
    return referee

@router.patch("/{referee_id}/password", response_model=RefereeLoad)
def update_referee_password(referee_id: int, referee_update: RefereeUpdate, db: Session = Depends(get_db)):
    db_referee = db.query(Referee).filter(Referee.id == referee_id).first()
    
    if not db_referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    
    if referee_update.password == db_referee.password:
        db_referee.password = referee_update.new_password
    else:
        raise HTTPException(status_code=401, detail="Unauthorized: Password does not match")
    
    db.commit()
    db.refresh(db_referee)
    return db_referee