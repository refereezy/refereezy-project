# Configuración de la base de dades
from sqlalchemy import create_engine, MetaData
from sqlalchemy.orm import sessionmaker, declarative_base
import os
from dotenv import load_dotenv
load_dotenv()

db_user = os.getenv("DB_USER")
db_password = os.getenv("DB_PASS")
db_port = os.getenv("DB_PORT")
db_name = os.getenv("DB_NAME")
db_host = os.getenv("DB_HOST")


# Configurar la conexión con PostgreSQL
DATABASE_URL = f"postgresql+psycopg2://{db_user}:{db_password}@{db_host}:{db_port}/{db_name}"

# Crear el motor de la base de dades
engine = create_engine(DATABASE_URL)

# Configurar la sesión de la base de dades
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Crear base para los modelos
Base = declarative_base() 
metadata = MetaData()
