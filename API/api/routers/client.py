from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Client
from schemas import ClientCreate, ClientResponse, ClientLogin
from dependencies import get_db
from typing import List
from utils import hash_password, verify_password
from datetime import timedelta, datetime


router = APIRouter(prefix="/clients", tags=["Clients"])

@router.get("/", response_model=List[ClientResponse])
def get_clients(db: Session = Depends(get_db)):
    return db.query(Client).all()

@router.post("/", response_model=ClientResponse)
def create_client(client: ClientCreate, db: Session = Depends(get_db)):
    hashed_pwd = hash_password(client.password)
    new_client = Client(**client.dict())
    new_client.password = hashed_pwd
    new_client.plan = "Eazy"
    new_client.plan_expiration = datetime.now() + timedelta(days=30)
    db.add(new_client)
    db.commit()
    db.refresh(new_client)
    new_client.password = '***'
    return new_client

@router.get("/{client_id}", response_model=ClientResponse)
def get_client(client_id: int, db: Session = Depends(get_db)):
    client = db.query(Client).filter(Client.id == client_id).first()
    if not client:
        raise HTTPException(status_code=404, detail="Client not found")
    return client

@router.put("/{client_id}", response_model=ClientResponse)
def update_client(client_id: int, client: ClientCreate, db: Session = Depends(get_db)):
    existing_client = db.query(Client).filter(Client.id == client_id).first()
    if not existing_client:
        raise HTTPException(status_code=404, detail="Client not found")
    for key, value in client.dict().items():
        setattr(existing_client, key, value)
    db.commit()
    db.refresh(existing_client)
    return existing_client

@router.delete("/{client_id}", response_model=dict)
def delete_client(client_id: int, db: Session = Depends(get_db)):
    client = db.query(Client).filter(Client.id == client_id).first()
    if not client:
        raise HTTPException(status_code=404, detail="Client not found")
    db.delete(client)
    db.commit()
    return {"message": "Client deleted successfully"}


@router.post("/login", response_model=ClientResponse)
def login(client_login: ClientLogin, db: Session = Depends(get_db)):
    client = db.query(Client).filter(Client.email == client_login.email).first()
    if not client or not verify_password(client_login.password, client.password):
        raise HTTPException(status_code=401, detail="Invalid email or password")
    return client