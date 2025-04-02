from fastapi import FastAPI,HTTPException
from routers import team, player, referee, match_group, match, client, clock
from fastapi.responses import JSONResponse
from sqlalchemy.exc import IntegrityError, DataError
from starlette.requests import Request
import traceback

app = FastAPI(title="Football API", version="1.0")


app.include_router(team.router)
app.include_router(player.router)
app.include_router(referee.router)
app.include_router(match_group.router)
app.include_router(match.router)
app.include_router(client.router)
app.include_router(clock.router)
#GET
@app.get("/")
def read_root():
    return "API REFEREEZY"


@app.exception_handler(IntegrityError)
async def integrity_error_handler(request: Request, exc: IntegrityError):
    traceback.print_exc()
    print(exc)
    return JSONResponse(
        status_code=400,
        content={"detail": "Integrity error occurred. Please check your data."}
    )

@app.exception_handler(DataError)
async def data_error_handler(request: Request, exc: DataError):
    traceback.print_exc()
    print(exc)
    return JSONResponse(
        status_code=400,
        content={"detail": "Data error occurred. Please check your data."}
    )

@app.exception_handler(Exception)
async def general_exception_handler(request: Request, exc: Exception):
    traceback.print_exc()
    print(exc)
    return JSONResponse(
        status_code=500,
        content={"detail": "An unexpected error occurred."}
    )
