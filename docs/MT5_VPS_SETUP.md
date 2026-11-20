# MT5 VPS Bridge — Setup Notes

## Current stage
The repository now contains a read-only MQL5 heartbeat connector and its API contract. It is a source-code foundation, not a deployed gateway. No connection will work until a real HTTPS gateway and per-terminal token are provisioned. The EA has not yet been compiled or tested in MetaEditor.

## Files
- `mt5/EdTradingProBridge.mq5` — read-only terminal heartbeat EA.
- `docs/MT5_GATEWAY_CONTRACT.md` — request schema, authentication and backend requirements.

## Safe setup sequence (after gateway deployment)
1. Copy `mt5/EdTradingProBridge.mq5` into the VPS terminal's `MQL5/Experts` folder.
2. Open it in MetaEditor and compile; resolve any compiler messages before attaching it.
3. In MT5, open Tools → Options → Expert Advisors.
4. Enable WebRequest only for the exact HTTPS gateway host supplied by the Ed Trading Pro deployment. Do not add wildcard hosts.
5. Attach the EA to one demo-account chart.
6. Set the gateway HTTPS URL, unique per-terminal token and a recognizable terminal alias.
7. Confirm the gateway reports fresh heartbeats and that the Android app displays the same terminal as connected.
8. Only after repeated reconnect and stale-heartbeat tests should we add symbol and position read-only synchronization.

## Important
- The EA does not place, modify or close trades.
- Do not send your MT5 password, investor password, VPS password or API token in chat.
- Do not install this on a live account until the EA is compiled, reviewed and tested on demo.
- The backend is not deployed yet, so do not enter a made-up URL or token.
