from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Referee, Client
from schemas import RefereeCreate, RefereeResponse, RefereeUpdate, RefereeLogin, RefereeLoad, RefereeData, MatchResponse
from dependencies import get_db
from typing import List
from datetime import datetime
from utils import hash_password, verify_password

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
    client = db.query(Client).filter(Client.id == client_id).first()
    if not client:
        raise HTTPException(status_code=404, detail="Client not found")
    return client.referees


@router.post("/", response_model=RefereeResponse)
def create_referee(referee: RefereeCreate, db: Session = Depends(get_db)):
    db_referee = Referee(**referee.dict(), token = tokenize_password(referee.password))
    db.add(db_referee)
    db.commit()
    db.refresh(db_referee)
    return db_referee


@router.put("/{referee_id}", response_model=RefereeResponse)
def update_referee(referee_id: int, referee: RefereeUpdate, db: Session = Depends(get_db)):
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


@router.get("/{referee_id}/matches", response_model=List[MatchResponse])
def get_referee_matches(referee_id: int, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.id == referee_id).first()
    if referee:
        today = datetime.now().date()
        referee.matches = [match for match in referee.matches if match.date.date() >= today]
    if not referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    
    return referee.matches


@router.post("/login", response_model=RefereeData)
def login_referee(creds: RefereeLogin, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.dni == creds.dni, Referee.password == creds.password).first()
    if not referee:
        raise HTTPException(status_code=401, detail="Invalid DNI or password")
    
    return referee


@router.post("/load", response_model=RefereeData)
def map_referee(creds: RefereeLoad, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.id == creds.id).first()
    
    if not referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    
    if verify_password(creds.token, referee.token):
        raise HTTPException(status_code=401, detail="Unauthorized: Invalid token")    
    
    return referee


@router.patch("/{referee_id}/password", response_model=RefereeData)
def update_referee_password(referee_id: int, referee_update: RefereeUpdate, db: Session = Depends(get_db)):
    referee = db.query(Referee).filter(Referee.id == referee_id).first()
    
    if not referee:
        raise HTTPException(status_code=404, detail="Referee not found")
    
    if verify_password(referee_update.token, referee.token):
        referee.password = referee_update.new_password
        referee.token = tokenize_password(referee_update.new_password)
    else:
        raise HTTPException(status_code=401, detail="Unauthorized: Password does not match")
    
    db.commit()
    db.refresh(referee)
    
    
    return referee


def tokenize_password(password: str) -> str:
    return hash_password(f"{password}_TOKEN")