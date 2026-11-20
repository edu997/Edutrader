# Ed Trading Pro — Android + MT5 Architecture

## Agreed product scope
- Continue the existing Spanish-language Edu Trader web app as a native Android application.
- Connect to MetaTrader 5 (MT5).
- Support all instruments exposed by the connected broker through MT5: forex, metals, indices, energy, shares, futures and crypto CFDs where offered.
- Support both manual tools/signals and automated Expert Advisor (EA) monitoring/control.
- Preserve useful existing features: dashboard, signal management, journal, performance statistics, risk planning and user/admin access.

## MT5 integration boundary
The Android app does not run an MT5 Expert Advisor. A supported MT5 desktop terminal on the user's VPS must remain connected to the broker. An MQL5 connector EA communicates with a secured backend gateway. The Android app communicates with the backend and never stores broker credentials.

Flow: MT5 terminal + EduTraderBridge EA → authenticated HTTPS gateway → database/event service → Android app. For commands, the gateway validates account ownership, permissions, freshness, idempotency and risk limits; the EA validates again, submits through MT5, and reports the broker-confirmed result. The app must distinguish pending, acknowledged, rejected and filled states.

First release defaults to read-only/demo mode. Live execution remains disabled until explicitly enabled with hard risk limits. Stale heartbeat, stale quotes or gateway outage must fail closed and block new automated entries.

## Proposed stack
- Android: Kotlin, Jetpack Compose, Material 3, Navigation Compose, Coroutines/Flow, Room and DataStore.
- Backend: authenticated API, PostgreSQL and realtime events. Supabase Auth/Postgres/Realtime/Edge Functions are a suitable option; the MT5 gateway remains a separate service.
- MT5 connector: MQL5 EA using a versioned JSON contract over HTTPS initially.
- Gateway: Python/FastAPI or equivalent, deployed separately with TLS, authorization, rate limits, audit log and health checks.
- Charts: chart-provider adapter; choose a licensed provider/library only after evaluating data rights and performance.
- Tests: Android unit/UI tests, API contract tests, EA demo-account tests and paper-trading end-to-end tests.

## Android modules
app/core/common — result types, errors, time and formatting
app/core/designsystem — Edu Trader theme, typography and reusable UI
app/core/network — authenticated API client
app/core/database — Room cache and migrations
app/core/security — token storage and session policy
app/feature/onboarding — setup and login
app/feature/dashboard — account summary and connection health
app/feature/markets — broker symbol discovery and watchlists
app/feature/charts — candles, timeframes, indicators and drawings
app/feature/signals — manual signals and alerts
app/feature/trading — order ticket, positions and pending orders
app/feature/automation — EA heartbeat, state, pause/resume and risk caps
app/feature/journal — binary journal plus general MT5 journal
app/feature/analytics — P/L, drawdown, expectancy and performance
app/feature/risk — sizing, exposure and daily limits
app/feature/news — calendar and event alerts
app/feature/profile and app/feature/admin — accounts, roles and settings

## Main navigation
Inicio; Mercados; Gráficos; Operar; Automatización; Señales; Diario; Estadísticas; Noticias; Perfil/Admin.

## Initial data model
users, profiles, roles, broker_connections, mt5_terminals, instruments, quotes, signals, orders, positions, ea_instances, risk_policies, trade_events, journal_entries, performance_snapshots, news_events and user_alerts.
Store money as decimal values, not floating-point. Store timestamps in UTC and localize in the client. Keep broker credentials out of the APK, source control and logs.

## Security and trading safeguards
- Bind each user to explicitly approved broker account IDs and terminal IDs.
- Use TLS, short-lived tokens, request signing, replay protection, idempotency keys and rate limits.
- Audit every order intent, validation, EA acknowledgement and broker response.
- Enforce max risk per trade, max daily loss, max exposure, max volume, allowed symbols and trading schedule on the backend and again in the EA.
- Emergency pause stops new entries; it must not silently close existing positions. Close-all is a separate explicit action with confirmation.
- Disconnection, stale data, market closure, invalid stops and broker rejection must block execution and show clear status.
- AI analysis is informational, never certainty, and cannot bypass risk controls.

## Migration strategy
Keep main unchanged. Develop on feature/android-mt5-foundation. Audit current HTML/JS screens, localStorage schemas, access controls and external services before replacing anything. Build native shell and feature parity first; add MT5 read-only connectivity before demo order execution.

## Release gates
0. Audit existing HTML app and data/security.
1. Android shell, theme, navigation, login and local persistence.
2. Dashboard, signals, journal, risk calculator and analytics parity.
3. MT5 demo read-only: heartbeat, symbols, quotes, positions and history.
4. Demo orders with dual risk checks and complete audit trail.
5. Controlled live pilot only after demo validation and explicit user approval.