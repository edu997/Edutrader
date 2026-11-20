# Ed Trading Pro — MT5 Gateway Contract (Read-Only v1)

## Purpose
Define the first communication contract between an MT5 terminal running on the user's VPS and Ed Trading Pro's future backend. This milestone is telemetry only. It must not accept or execute trading commands.

## Network flow
MT5 terminal + EdTradingProBridge.mq5 → outbound HTTPS POST → authenticated gateway → database/event stream → Android app.

The MT5 terminal initiates the connection. No inbound VPS port needs to be opened for this heartbeat design.

## Endpoint
`POST /v1/mt5/heartbeat`

Headers:
- `Content-Type: application/json`
- `Authorization: Bearer <per-terminal-token>`

The token is issued per terminal, revocable, scoped only to heartbeat ingestion, and stored as a configurable EA input. Never commit a real token to Git or share it in chat.

## Request example
```json
{
  "schema_version": 1,
  "terminal_alias": "My MT5 VPS",
  "account_login": 12345678,
  "account_server": "Broker-Demo",
  "broker_company": "Broker Ltd",
  "account_currency": "USD",
  "balance": 1000.00,
  "equity": 1000.00,
  "margin": 0.00,
  "free_margin": 1000.00,
  "leverage": 100,
  "terminal_build": 5000,
  "terminal_connected": true
}
```

The values above are illustrative only. The Android app must never display them as a real account until received from an authenticated terminal.

## Gateway requirements
- Require TLS; reject plain HTTP.
- Validate token, token scope, terminal ownership and schema version.
- Rate-limit per terminal and reject oversized payloads.
- Store server-received timestamp separately from terminal-reported data.
- Do not log bearer tokens or sensitive request headers.
- Treat stale heartbeat as disconnected; suggested initial stale threshold: 90 seconds for a 30-second interval.
- Do not trust account_login alone as proof of ownership. Bind terminal ID and approved account ID during explicit setup.
- Return 202 Accepted for a valid heartbeat; 401/403 for invalid credentials/ownership; 422 for invalid payload; 429 for rate limit.
- Keep account identifiers private; expose a masked account label in mobile UI unless the user explicitly chooses otherwise.

## First milestone boundaries
Included: connection heartbeat, broker/server identity, account currency, balance/equity/margin telemetry, terminal build and connected state.

Not included: symbol discovery, quote streaming, positions/history sync, signals, EA control, or any order operation. These are separate versioned milestones.

## Operational safety
- This connector is read-only and contains no order send/modify/close calls.
- If gateway URL or token is empty, it sends nothing.
- Configure the exact HTTPS host in MT5: Tools → Options → Expert Advisors → Allow WebRequest for listed URL.
- Test on a demo account first.
- Do not disable VPS firewall or expose the MT5 terminal to inbound internet traffic for this connector.
