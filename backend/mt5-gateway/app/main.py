"""Read-only MT5 heartbeat gateway for Ed Trading Pro."""
from __future__ import annotations

import hmac
import json
import os
from contextlib import asynccontextmanager
from datetime import datetime, timezone
from typing import Annotated

import psycopg
from fastapi import FastAPI, Header, HTTPException, status
from pydantic import BaseModel, ConfigDict, Field

DATABASE_URL = os.environ.get("DATABASE_URL", "")
# JSON object mapping terminal alias -> unique long bearer token.
TERMINAL_TOKENS = json.loads(os.environ.get("MT5_TERMINAL_TOKENS", "{}"))


class Heartbeat(BaseModel):
    model_config = ConfigDict(extra="forbid", str_strip_whitespace=True)

    terminal_alias: str = Field(min_length=1, max_length=64)
    login: str = Field(min_length=1, max_length=32)
    server: str = Field(min_length=1, max_length=128)
    company: str = Field(default="", max_length=128)
    currency: str = Field(min_length=1, max_length=12)
    balance: float = Field(ge=0, allow_inf_nan=False)
    equity: float = Field(ge=0, allow_inf_nan=False)
    margin: float = Field(ge=0, allow_inf_nan=False)
    free_margin: float = Field(ge=0, allow_inf_nan=False)
    leverage: int = Field(ge=1, le=100000)
    terminal_build: int = Field(ge=1)
    terminal_connected: bool
    terminal_company: str = Field(default="", max_length=128)


def db_connection():
    if not DATABASE_URL:
        raise RuntimeError("DATABASE_URL is not configured")
    return psycopg.connect(DATABASE_URL, connect_timeout=5)


@asynccontextmanager
async def lifespan(_: FastAPI):
    if DATABASE_URL:
        with db_connection() as conn:
            conn.execute("""
                CREATE TABLE IF NOT EXISTS mt5_heartbeats (
                    id BIGSERIAL PRIMARY KEY,
                    terminal_alias VARCHAR(64) NOT NULL,
                    received_at TIMESTAMPTZ NOT NULL,
                    payload JSONB NOT NULL
                )
            """)
            conn.execute("""
                CREATE INDEX IF NOT EXISTS idx_mt5_heartbeats_alias_received
                ON mt5_heartbeats (terminal_alias, received_at DESC)
            """)
    yield


app = FastAPI(
    title="Ed Trading Pro MT5 Gateway",
    version="0.1.0",
    docs_url=None,
    redoc_url=None,
    lifespan=lifespan,
)


@app.get("/healthz")
def healthz():
    return {"status": "ok", "service": "ed-trading-pro-mt5-gateway"}


def authenticate(payload: Heartbeat, authorization: str | None) -> None:
    expected = TERMINAL_TOKENS.get(payload.terminal_alias)
    if not expected or not authorization:
        raise HTTPException(status_code=401, detail="Unauthorized")
    scheme, _, supplied = authorization.partition(" ")
    if scheme.lower() != "bearer" or not supplied:
        raise HTTPException(status_code=401, detail="Unauthorized")
    if not hmac.compare_digest(expected, supplied):
        raise HTTPException(status_code=401, detail="Unauthorized")


@app.post("/v1/mt5/heartbeat", status_code=status.HTTP_202_ACCEPTED)
def receive_heartbeat(
    payload: Heartbeat,
    authorization: Annotated[str | None, Header()] = None,
):
    authenticate(payload, authorization)
    received_at = datetime.now(timezone.utc)  # server time; never trust VPS clock

    try:
        with db_connection() as conn:
            conn.execute(
                """
                INSERT INTO mt5_heartbeats (terminal_alias, received_at, payload)
                VALUES (%s, %s, %s::jsonb)
                """,
                (payload.terminal_alias, received_at, json.dumps(payload.model_dump())),
            )
    except (psycopg.Error, RuntimeError):
        raise HTTPException(status_code=503, detail="Heartbeat storage unavailable")

    return {
        "accepted": True,
        "terminal_alias": payload.terminal_alias,
        "received_at": received_at.isoformat(),
    }
