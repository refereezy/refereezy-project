from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Referee
from schemas import RefereeCreate, RefereeResponse, RefereeUpdate, RefereeLogin
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

@router.get("/load/{referee_id}/{password}", response_model=RefereeResponse)
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
    one_month_ago = datetime.now() - timedelta(days=30)
    referee = db.query(Referee).filter(Referee.id == referee_id).first()
    if referee:
        referee.matches = [match for match in referee.matches if match.date >= one_month_ago]
    if not referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    return referee.matches

@router.post("/login", response_model=RefereeResponse)
def login_referee(referee: RefereeLogin, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.dni == referee.dni, Referee.password == referee.password).first()
    if not referee:
        raise HTTPException(status_code=401, detail="Invalid DNI or password")
    return referee

@router.patch("/{referee_id}/password", response_model=RefereeResponse)
def update_referee_password(referee_id: int, referee_update: RefereeUpdate, db: Session = Depends(get_db)):
    db_referee = db.query(Referee).filter(Referee.id == referee_id).first()
    
    if not db_referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    
    if referee_update.password == db_referee.password:
        db_referee.password = referee_update.password
    else:
        raise HTTPException(status_code=401, detail="Unauthorized: Password does not match")
    
    db.commit()
    db.refresh(db_referee)
    return db_referee