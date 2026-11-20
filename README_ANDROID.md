# GP Trading Pro — Android

Native Android foundation for the Edu Trader Pro evolution.

## Branding and scope
- App name: GP Trading Pro
- Visual identity: premium dark navy and gold
- Accounts: demo and real account modes are represented in the UI. Real trading is intentionally unavailable until MT5 connectivity, account authorization and risk safeguards are implemented.
- Platform: MetaTrader 5
- Markets: instruments exposed by the connected broker through MT5
- Modes: manual trading tools/signals and EA monitoring/control

## Open the project
Open the repository root in Android Studio (Hedgehog or newer with a compatible Android Gradle Plugin), install Android SDK 36 and JDK 17, then sync Gradle and run the app on an Android 8.0+ device or emulator.

The repository does not yet include a Gradle wrapper. Android Studio can import the Gradle project; a wrapper should be generated and committed in the next build-validation pass.

## Current implementation
- Native Kotlin + Jetpack Compose app shell
- Navy/gold design system starter
- Bottom navigation for Inicio, Mercados, Operar, Bot IA and Diario
- Demo/real account-mode presentation
- Honest disconnected states; no fabricated balance, quotes or P/L
- No order execution or broker credentials in the app

## Next milestones
1. Audit and map existing web app features and localStorage data before migration.
2. Implement persistence, secure authentication and account model.
3. Rebuild dashboard, signals, risk calculator, journal and analytics.
4. Build the MT5 read-only gateway and MQL5 connector for a demo terminal.
5. Add demo-only order execution with dual-layer risk controls and audit logs.
6. Enable real-account support only after successful demo testing and explicit user setup.

See `docs/ANDROID_ARCHITECTURE.md` and `docs/ANDROID_ROADMAP.md`.
