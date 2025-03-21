from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Referee
from schemas import RefereeCreate, RefereeResponse
from dependencies import get_db
from typing import List

router = APIRouter(prefix="/referees", tags=["Referees"])

@router.get("/", response_model=List[RefereeResponse])
def get_referees(db: Session = Depends(get_db)):
    return db.query(Referee).all()

@router.get("/{referee_id}", response_model=RefereeResponse)
def get_referee(referee_id: int, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.id == referee_id).first()
    if not referee:
        raise HTTPException(status_code=404, detail="Referee not found")
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