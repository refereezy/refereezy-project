from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from dependencies import get_db
from models import Referee
from schemas import ClockSchema
import random, string

router = APIRouter(tags=["Pairing"])



@router.get("/clock/generate", response_model=ClockSchema)
def gen_code(db: Session = Depends(get_db)):
  
  code = ''.join(random.choices(string.ascii_letters + string.digits, k=50))
  
  return {"code": code}

@router.post("/assignTo/{referee_id}")
def assign_code(referee_id: int, clock: ClockSchema, db: Session = Depends(get_db)):
  
  referee = db.query(Referee).filter(Referee.id == referee_id).first()
  
  if not referee:
    raise HTTPException(status_code=404, detail="Referee not found")
  
  referee.clock_code = clock.code
  
  db.commit()
  db.refresh(referee)
  
  return {"message": "Clock code assigned successfully", "referee_id": referee_id}

@router.delete("/revoke/{referee_id}")
def revoke_code(referee_id: int, db: Session = Depends(get_db)):
  # Check if the referee exists
  referee = db.query(Referee).filter(Referee.id == referee_id).first()
  if not referee:
    raise HTTPException(status_code=404, detail="Referee not found")
  
  # Check if the referee has a clock code assigned
  if not referee.clock_code:
    raise HTTPException(status_code=400, detail="Referee does not have a clock code assigned")
  
  # Remove the code from the referee
  referee.clock_code = None
  db.commit()
  
  return {"message": "Clock code revoked successfully", "referee_id": referee_id}
  

