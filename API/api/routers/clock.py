from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from dependencies import get_db
from models import Clock, Referee
from schemas import ClockSchema
import random, string

router = APIRouter(tags=["Pairing"])



@router.get("/clock/generate", response_model=ClockSchema)
def gen_code(db: Session = Depends(get_db)):
  attemps = 0
  while True:
    code = ''.join(random.choices(string.ascii_letters + string.digits, k=50))
    
    clock = Clock()
    clock.code = code
    
    try:
      db.add(clock)
      db.commit()
      return {"code": code}
    except Exception as e:
      db.rollback()
      attemps+=1
      # Retry generating and adding a new code
      if (attemps == 10):
        raise HTTPException(status_code=500, detail="Failed to generate a unique clock code")
      
      continue
    
@router.post("/assignTo/{referee_id}")
def assign_code(referee_id: int, clock: ClockSchema, db: Session = Depends(get_db)):
  
  referee = db.query(Referee).filter(Referee.id == referee_id).first()
  
  if not referee:
    raise HTTPException(status_code=404, detail="Referee not found")
  
  db_clock = db.query(Clock).filter(Clock.code == clock.code).count()
  
  if db_clock == 0:
    raise HTTPException(status_code=404, detail="Clock code non existent")
  
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
  
  # Find the clock with the assigned code
  clock = db.query(Clock).filter(Clock.code == referee.clock_code).first()
  if not clock:
    raise HTTPException(status_code=404, detail="Clock code not found")
  
  # Delete the clock and remove the code from the referee
  db.delete(clock)
  referee.clock_code = None
  db.commit()
  
  return {"message": "Clock code revoked successfully", "referee_id": referee_id}
  

