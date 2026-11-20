# Ed Trading Pro — Brand and Account Rollout

## Confirmed product decisions
- Public app name: Ed Trading Pro
- Continue the existing Edu Trader Pro web project as the product foundation.
- Visual direction: premium dark navy and gold.
- Supported account types in the product plan: demo and real.
- Trading platform: MetaTrader 5.
- Market coverage: all instruments exposed by the user's connected MT5 broker.
- Operating modes: manual tools/signals and automated EA monitoring/control.

## Account rollout policy
Both demo and real accounts are part of the intended product. The initial Android shell only presents account-mode selection; it is not yet connected to a broker and does not authenticate a trading account. Real-account order execution remains disabled until the MT5 connector, backend authorization, dual-layer risk checks, audit trail and demo validation are complete.

## Branding tokens
- App background: #08111F
- Main surface: #111E30
- Elevated surface: #17263A
- Gold accent: #D6AE57
- Primary text: #F5F7FA
- Muted text: #91A0B5
- Positive: #35C58A
- Negative: #FF6B72

## Important distinction
The current web app includes binary-options workflows. Those journal records and CALL/PUT concepts must be preserved as a separate feature. MT5 market orders use BUY/SELL, volume, broker symbols, stop loss, take profit, positions and broker tickets; do not merge the two data models.
