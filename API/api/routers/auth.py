# routers/auth.py
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Client
from schemas import ClientCreate, ClientResponse, ClientLogin
from dependencies import get_db
from typing import List
from utils import verify_password

router = APIRouter(prefix="/auth", tags=["Authentication"])

@router.post("/login")
def login(user: ClientLogin, db: Session = Depends(get_db)):
    db_user = db.query(Client).filter(Client.email == user.email).first()
    
    if not db_user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    if not verify_password(user.password, db_user.hashed_password):
        raise HTTPException(status_code=401, detail="Contraseña incorrecta")
    
    return {"message": "Inicio de sesión exitoso", "user_id": db_user.id}
