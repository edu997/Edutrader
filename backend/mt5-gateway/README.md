# Ed Trading Pro — MT5 heartbeat gateway (read-only)

This service receives the initial MT5 terminal heartbeat. It exposes no trade
execution, order modification, position closing, or withdrawal endpoints.

## Environment

- `DATABASE_URL`: PostgreSQL connection string for a managed database.
- `MT5_TERMINAL_TOKENS`: JSON object mapping terminal alias to a unique,
  high-entropy bearer token, e.g. `{"my-vps-mt5":"REPLACE_WITH_RANDOM_SECRET"}`.

Generate secrets with a cryptographically secure generator and store them only
in the hosting provider's secret manager. Never commit a token, paste it into
chat, or embed it in the Android app. Use a distinct token per terminal.

## Local run

```bash
python -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
export DATABASE_URL='postgresql://...'
export MT5_TERMINAL_TOKENS='{"my-vps-mt5":"local-test-secret"}'
uvicorn app.main:app --reload
```

The table and index are created at startup. Routes: `POST /v1/mt5/heartbeat`
and non-diagnostic liveness `GET /healthz`.

## Deployment checklist

1. Deploy this container on an external hosting platform, not on the 2 GB MT5 VPS.
2. Attach managed PostgreSQL; store both environment values as provider secrets.
3. Serve it through HTTPS only.
4. Configure provider-level request-size limits, rate limiting, monitoring, backups,
   and restricted database network access.
5. Only after deployment, add the exact HTTPS host to the MT5 EA WebRequest allowlist.
6. First connect a separate demo terminal or controlled demo account. Do not attach
   the EA to the live terminal until the heartbeat is reviewed.

## Safety and limitations

- Pydantic validates the payload and rejects unknown fields.
- Each terminal alias has its own bearer token; comparison is constant-time.
- The gateway assigns `received_at`; it does not trust the VPS clock.
- Responses do not expose secrets or database internals.
- Account summary fields are stored in PostgreSQL; restrict access and retention.
- This is an initial scaffold, not deployed or externally security-audited.
- No live trading or order-control functionality is present.
