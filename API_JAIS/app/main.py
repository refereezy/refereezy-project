from fastapi import FastAPI,HTTPException
from routers import team, player, referee, match_group, match, client

app = FastAPI(title="Football API", version="1.0")


app.include_router(team.router)
app.include_router(player.router)
app.include_router(referee.router)
app.include_router(match_group.router)
app.include_router(match.router)
app.include_router(client.router)
#GET
@app.get("/")
def read_root():
    return {"API REFEREEZY"}


