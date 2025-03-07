 # Configuración de la base de dades
from sqlalchemy import create_engine, MetaData
from sqlalchemy.orm import sessionmaker, declarative_base

# Configurar la conexión con PostgreSQL
DATABASE_URL = "postgresql+psycopg2://postgres:system@localhost:5433/refereezy"

# Crear el motor de la base de dades
engine = create_engine(DATABASE_URL)

# Configurar la sesión de la base de dades
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Crear base para los modelos
Base = declarative_base() 
metadata = MetaData()
