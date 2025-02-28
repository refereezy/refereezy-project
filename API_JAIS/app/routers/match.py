from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Match
from schemas import MatchCreate, MatchResponse
from dependencies import get_db
from typing import List

router = APIRouter(prefix="/matches", tags=["Matches"])

@router.get("/", response_model=List[MatchResponse])
def get_matches(db: Session = Depends(get_db)):
    return db.query(Match).all()
