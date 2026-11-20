# Edu Trader Pro — Android Development Roadmap

## Existing baseline found
The main branch contains a large single-file Spanish-language web app in index.html titled “Binomo Pro — EduTrader”. It includes a PIN/session screen, dashboard and admin-related UI, signal handling/history, planning and daily logging, localStorage persistence, and a separate binary-options journal with risk limits and performance summaries. It is a web app, not an Android/Gradle project. README.md and package.json were not present at the checked paths.

Binary-specific concepts (CALL/PUT, payout, expiry) must remain separate from MT5 concepts (BUY/SELL, volume, SL/TP, positions and broker tickets).

## Agreed scope
- Continue Edu Trader Pro.
- Native Android application.
- MetaTrader 5 integration.
- Broker-available instruments across asset classes.
- Both manual tools/signals and automated EA monitoring/control.

## Phase 0 — Audit
- Map current tabs, screens, roles and admin actions in index.html.
- Inventory localStorage keys and data schemas.
- Identify external services, API keys, database URLs and secrets; rotate exposed secrets if necessary.
- Separate binary-options-only logic from MT5 order concepts.
- Document responsive behavior and known defects.

## Phase 1 — Android foundation
- Create Gradle Kotlin project with Jetpack Compose and Material 3.
- Establish module boundaries, theme, navigation, dependency injection and build variants.
- Add login/session architecture and secure token handling.
- Add Room entities, migrations and repository interfaces.
- Add Spanish localization and accessibility/contrast rules.

## Phase 2 — Feature parity
- Implement dashboard, signal inbox, daily plan, risk calculator, journal and statistics.
- Import/export existing user data; never silently discard localStorage data.
- Keep binary journal records separate from MT5 trade records.

## Phase 3 — MT5 read-only connector
- Define a versioned API contract.
- Build gateway and MQL5 connector.
- Verify terminal heartbeat, account alias, broker symbols, quotes, positions and trade history on a demo account.
- Handle reconnects, stale data, symbol suffixes and broker-specific contract specifications.

## Phase 4 — Demo execution and automation
- Add manual demo order ticket and EA command channel.
- Add dual-layer risk checks, idempotency, expiring commands and audit trail.
- Add pause/resume and emergency stop for new entries.
- Test rejected orders, disconnections, duplicate messages, market closures and invalid stops.

## Phase 5 — Live pilot
- Disabled by default.
- Require explicit account binding, risk policy, confirmation and a successful demo test report.
- Roll out to one account first and monitor with a clear pause control.

## Immediate engineering deliverable
Build the native Android project skeleton and instrument-independent domain model. Complete Phase 0 first so existing users' data and access controls are not broken.