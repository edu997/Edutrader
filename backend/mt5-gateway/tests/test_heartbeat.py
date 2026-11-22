import sys
from pathlib import Path

import pytest
from fastapi import HTTPException
from pydantic import ValidationError

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.main import Heartbeat, authenticate


VALID = {
    "terminal_alias": "demo-vps",
    "login": "123456",
    "server": "Broker-Demo",
    "company": "Broker",
    "currency": "USD",
    "balance": 1000.0,
    "equity": 1000.0,
    "margin": 0.0,
    "free_margin": 1000.0,
    "leverage": 100,
    "terminal_build": 5000,
    "terminal_connected": True,
    "terminal_company": "Broker",
}


def test_heartbeat_accepts_valid_read_only_payload():
    payload = Heartbeat.model_validate(VALID)
    assert payload.terminal_alias == "demo-vps"
    assert payload.terminal_connected is True


def test_heartbeat_rejects_unexpected_fields():
    with pytest.raises(ValidationError):
        Heartbeat.model_validate({**VALID, "password": "must-not-be-accepted"})


def test_heartbeat_rejects_negative_balance():
    with pytest.raises(ValidationError):
        Heartbeat.model_validate({**VALID, "balance": -1})


def test_authentication_rejects_missing_token():
    payload = Heartbeat.model_validate(VALID)
    with pytest.raises(HTTPException) as exc:
        authenticate(payload, None)
    assert exc.value.status_code == 401


def test_authentication_rejects_wrong_token(monkeypatch):
    import app.main as gateway

    monkeypatch.setattr(gateway, "TERMINAL_TOKENS", {"demo-vps": "expected-secret"})
    payload = Heartbeat.model_validate(VALID)
    with pytest.raises(HTTPException) as exc:
        authenticate(payload, "Bearer incorrect-secret")
    assert exc.value.status_code == 401


def test_authentication_accepts_matching_token(monkeypatch):
    import app.main as gateway

    monkeypatch.setattr(gateway, "TERMINAL_TOKENS", {"demo-vps": "expected-secret"})
    payload = Heartbeat.model_validate(VALID)
    authenticate(payload, "Bearer expected-secret")
