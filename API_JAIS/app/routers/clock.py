from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from dependencies import get_db
from models import Temp2FA, Clock
import random
import string
from datetime import datetime, timedelta
from pydantic import BaseModel

router = APIRouter(tags=["Pairing"])

class Temp2FAData(BaseModel):
  referee_id: int
  clock_code: str
  

@router.get("/clock/generate")
def gen_code(db: Session = Depends(get_db)):
  code = ''.join(random.choices(string.ascii_letters + string.digits, k=5))
  
  clock = Clock()
  clock.code = code
  
  try:
    db.add(clock)
    db.commit()
  except Exception as e:
    db.rollback()
  
  return { "code": code }

@router.post("/2fa/gen-for/{referee_id}/{clock_code}")
def gen_2fa(referee_id: int, clock_code: str, db: Session = Depends(get_db)):
  
  temp_2fa = db.query(Temp2FA).filter(Temp2FA.referee_id == referee_id).first()
  
  if not temp_2fa:
    temp_2fa = Temp2FA()
    temp_2fa.referee_id = referee_id
    temp_2fa.twofa_code = random.randint(1, 99)
    temp_2fa.clock_code = clock_code
    temp_2fa.expiration = datetime.utcnow() + timedelta(minutes=10)
    temp_2fa.paired = False
    
    db.add(temp_2fa)
    
  else:
    temp_2fa.twofa_code = random.randint(1, 99)
    temp_2fa.clock_code = clock_code
    temp_2fa.expiration = datetime.utcnow() + timedelta(minutes=10)
    temp_2fa.paired = False
  
  db.commit()
  db.refresh(temp_2fa)
  
  return {"twofa_code": temp_2fa.twofa_code}
  

@router.put("/2fa/assign/{referee_id}/{clock_code}")
def assign_2fa(referee_id: int, clock_code: str, db: Session = Depends(get_db)): 
  
  temp_2fa = db.query(Temp2FA).filter(Temp2FA.referee_id == referee_id).first()
  
  if not temp_2fa:
    raise HTTPException(status_code=404, detail="2FA not found")
  
  temp_2fa.clock_code = clock_code
  temp_2fa.paired = True
  
  db.commit()
  db.refresh(temp_2fa)
  
  return {"message": "Clock assigned to 2FA"}
  


@router.delete("/2fa/{referee_id}")
def delete_2fa(referee_id: int, db: Session = Depends(get_db)):
  obj = db.query(Temp2FA).filter(Temp2FA.referee_id == referee_id).first()
  clock = db.query(Clock).filter(Clock.code == obj.clock_code).first()
  db.delete(clock)
  db.commit()
  
  return {"message": "2FA deleted"}
  

@router.get("/2fa/{clock_code}")
def get_2fa(clock_code: str, db: Session = Depends(get_db)):
  temp_2fa = db.query(Temp2FA).filter(
    Temp2FA.clock_code == clock_code, (Temp2FA.paired == True) | (Temp2FA.expiration > datetime.utcnow())
  ).first()

  if not temp_2fa:
    raise HTTPException(status_code=404, detail="2FA not found")

  return temp_2fa

