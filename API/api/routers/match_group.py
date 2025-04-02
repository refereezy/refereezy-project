from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import MatchGroup
from schemas import MatchGroupCreate, MatchGroupResponse
from dependencies import get_db
from typing import List

router = APIRouter(prefix="/match_groups", tags=["Match Groups"])

@router.get("/", response_model=List[MatchGroupResponse])
def get_match_groups(db: Session = Depends(get_db)):
    return db.query(MatchGroup).all()
