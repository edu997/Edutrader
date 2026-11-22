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