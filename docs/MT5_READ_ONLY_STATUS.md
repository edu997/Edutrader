# MT5 read-only connection status

The Android app must not infer that MT5 is connected merely because a terminal was configured.

## Status rules

- NOT_CONFIGURED: no authenticated heartbeat has been received.
- OFFLINE: the latest heartbeat says the MT5 terminal is disconnected.
- STALE: the heartbeat is older than 90 seconds, or its timestamp is in the future.
- CONNECTED: the terminal reports connected and the heartbeat is recent.

The Android domain model evaluates these states from a gateway-verified receive timestamp. The backend must authenticate the terminal token, validate the payload, and assign the authoritative receive timestamp. Never trust a timestamp supplied by the EA as the sole freshness signal.

## Safety boundary

A CONNECTED status means only that a recent terminal heartbeat was received. It does not mean the broker is accepting orders, a symbol is tradable, the account mode is verified, the EA is authorized to trade, or the Android app can execute or modify an order.

The current integration remains read-only. Live trading and order commands remain disabled.