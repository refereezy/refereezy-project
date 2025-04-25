from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
from models import Team
from schemas import TeamCreate, TeamResponse
from dependencies import get_db
from typing import List
import cloudinary
import os
from dotenv import load_dotenv
import cloudinary.uploader

load_dotenv()

cloudinary.config( 
    cloud_name=os.getenv("CLOUDINARY_NAME"), 
    api_key=os.getenv("CLOUDINARY_API_KEY"), 
    api_secret=os.getenv("CLOUDINARY_API_SECRET"), 
    secure=True
)

router = APIRouter(prefix="/teams", tags=["Teams"])

# Get Pare
@router.get("/", response_model=List[TeamResponse])
def get_teams(db: Session = Depends(get_db)):
    return db.query(Team).all()

#GET Per ID
@router.get("/{team_id}", response_model=TeamResponse)
def get_team(team_id: int, db: Session = Depends(get_db)):
    team = db.query(Team).filter(Team.id == team_id).first()
    if not team:
        raise HTTPException(status_code=404, detail="Team not found")
    return team

@router.get("/client/{client_id}", response_model=List[TeamResponse])
def get_teams_by_client(client_id: int, db: Session = Depends(get_db)):
    teams = db.query(Team).filter(Team.client_id == client_id).all()
    if not teams:
        raise HTTPException(status_code=404, detail="No teams found for the given client")
    return teams

# Post Team
@router.post("/teams", response_model=TeamResponse)
async def create_team(
    team: TeamCreate = Depends(TeamCreate.as_form),
    logo: UploadFile = File(...),
    db: Session = Depends(get_db)
):
    # Subir imagen a Cloudinary
    upload_result = cloudinary.uploader.upload(logo.file, folder="logos_equipos/")
    logo_url = upload_result.get("secure_url")

    # Guardar en la base de datos
    db_team = Team(**team.dict(), logo_url=logo_url)
    db.add(db_team)
    db.commit()
    db.refresh(db_team)

    return db_team

@router.put("/{team_id}", response_model=TeamResponse)
def update_team(team_id: int, team: TeamCreate, db: Session = Depends(get_db)):
    db_team = db.query(Team).filter(Team.id == team_id).first()
    if not db_team:
        raise HTTPException(status_code=404, detail="Team not found")
    for key, value in team.dict().items():
        setattr(db_team, key, value)
    db.commit()
    db.refresh(db_team)
    return db_team

@router.delete("/{team_id}")
def delete_team(team_id: int, db: Session = Depends(get_db)):
    db_team = db.query(Team).filter(Team.id == team_id).first()
    if not db_team:
        raise HTTPException(status_code=404, detail="Team not found")
    db.delete(db_team)
    db.commit()
    return {"message": "Team deleted"}

