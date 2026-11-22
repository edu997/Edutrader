# ForexCheapVPS deployment notes

These notes adapt the MT5 bridge rollout to the provider named by the project owner: ForexCheapVPS. The provider's public FAQ describes a managed Windows VPS with Windows Server 2022, full Remote Desktop access, and support for MT5 and Expert Advisors. Exact server resources, region, Windows build, and account configuration must be confirmed in the customer's own service portal.

## Non-disruption rule

The existing VPS runs the owner's live MT5 automation. Do not reboot, migrate, update Windows, reinstall MT5, change broker credentials, disable Algo Trading, remove existing charts, or modify existing EAs as part of this integration.

Prefer a separate MT5 terminal installation and a separate demo login for the first bridge test. If a separate terminal is not practical, obtain the owner's explicit approval before attaching the read-only bridge EA to the existing terminal. Never attach it to an active trading chart as an unannounced change.

## Preparation checklist

1. Confirm the VPS is reachable using the provider's Remote Desktop details. Do not share the RDP password, MT5 password, or terminal token in chat or GitHub.
2. Record (without publishing secrets) the VPS Windows version, MT5 terminal build, broker server name, server region, and available free RAM/CPU.
3. Back up the existing MT5 `MQL5/Experts`, `MQL5/Profiles`, `MQL5/Presets`, and terminal configuration before making any change. Keep a copy outside the VPS.
4. Confirm whether a second MT5 terminal instance can be installed without changing the current installation. Use a demo account for the first end-to-end test.
5. Do not configure the EA yet: the HTTPS gateway endpoint and per-terminal token have not been deployed or issued.

## Bridge rollout after gateway deployment

1. Install/compile `mt5/EdTradingProBridge.mq5` in the separate test terminal using MetaEditor. Compilation must be checked locally; repository source alone is not proof that the EA compiles.
2. In MT5, add only the exact HTTPS gateway host supplied by the project deployment to Tools > Options > Expert Advisors > Allow WebRequest for listed URL. Do not add broad wildcard hosts.
3. Configure the bridge's gateway URL, terminal alias, and terminal-specific bearer token in the test terminal. Never commit the token or include it in screenshots, logs, support tickets, or source control.
4. Attach the bridge to a dedicated chart in the demo terminal. Confirm the terminal's Experts and Journal tabs show successful heartbeat responses and no repeated errors.
5. Verify the backend marks the heartbeat as received using its own server timestamp. Only then may Android display a recent read-only connection state.
6. Observe CPU, RAM, MT5 stability, and the existing bot before considering any production-terminal rollout.

## Network and support

ForexCheapVPS advertises 24/7 support for Remote Desktop, Windows Server, connectivity, and platform installation. If outbound HTTPS is blocked or the terminal cannot reach the gateway, ask provider support whether outbound TCP 443 is permitted for the VPS and whether any network policy is blocking the destination. Give support the gateway hostname only after it exists; never provide terminal tokens or trading passwords.

## Explicit limitations

- No gateway has been deployed yet; do not invent a URL or token.
- The current EA is intended for read-only heartbeat reporting and has not been compiled or validated on this VPS.
- No MT5 orders, positions, history, quotes, or symbols are exposed by this initial heartbeat-only bridge.
- Do not claim the Android app is connected until authenticated heartbeats are received and freshness is verified.

## Provider references

- ForexCheapVPS FAQ: https://www.forexcheapvps.com/faqs
- ForexCheapVPS support: https://www.forexcheapvps.com/supports

## Low-resource profile: 2 GB RAM

The owner has confirmed that this VPS plan has 2 GB of RAM. Treat it as a constrained production machine because Windows Server, the existing MT5 terminal, and the live EA/bot already share that memory.

- Do not install or run a second MT5 terminal on this VPS for initial testing. A second terminal can materially increase RAM and CPU pressure.
- Do not host the Ed Trading Pro API, database, dashboard, or other services on this 2 GB VPS. Host the gateway/backend externally (managed hosting or a separate server); the VPS should remain an MT5 terminal host only.
- Do not add monitoring agents, Docker, a local database, or memory-heavy utilities as part of this rollout.
- Keep the existing MT5 terminal, charts, EAs, and Windows configuration unchanged. Before any bridge test, check Task Manager for available memory and establish a baseline while the current bot is operating normally.
- The bridge EA is designed to send a small HTTPS heartbeat periodically, but its actual resource use has not yet been measured on this machine. Do not assume it is harmless simply because the payload is small.
- The first test must be scheduled during a maintenance window, use a demo terminal if the provider can supply a separate VPS or isolated test environment, and include a clear rollback: remove the bridge EA from its test chart and restore the prior chart/profile state.
- If no separate test environment exists, do not attach the EA to the live terminal until the owner explicitly approves the risk and there is a tested rollback plan. Never stop the live bot merely to make room for an unapproved test.

Suggested acceptance criteria before any production-terminal trial: no sustained increase in CPU usage, no material reduction in free memory, no MT5 freezes/restarts, no missed bot activity, and successful authenticated heartbeat delivery. If memory pressure or instability appears, remove the bridge EA and stop the trial.

**Recommendation for this 2 GB plan:** keep all Ed Trading Pro backend services off the VPS, and postpone live-terminal bridge installation until a separate demo/test environment or an approved maintenance window is available. The current read-only design remains the only intended first phase; order execution is out of scope.
