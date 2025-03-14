from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Client
from schemas import ClientCreate, ClientResponse
from dependencies import get_db
from typing import List

router = APIRouter(prefix="/clients", tags=["Clients"])

@router.get("/", response_model=List[ClientResponse])
def get_clients(db: Session = Depends(get_db)):
    return db.query(Client).all()
