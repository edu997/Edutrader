package com.gptradingpro.app.domain

import java.math.BigDecimal
import java.time.Instant

enum class AccountMode { DEMO, REAL }
enum class AssetClass { FOREX, METAL, INDEX, ENERGY, SHARE, FUTURE, CRYPTO, OTHER }
enum class TradeSide { BUY, SELL }
enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED, STALE, ERROR }

data class Instrument(
    val brokerSymbol: String,
    val displayName: String,
    val assetClass: AssetClass,
    val digits: Int,
    val contractSize: BigDecimal?,
    val isTradable: Boolean
)

data class Quote(
    val brokerSymbol: String,
    val bid: BigDecimal,
    val ask: BigDecimal,
    val timestamp: Instant
)

data class AccountSnapshot(
    val accountAlias: String,
    val mode: AccountMode,
    val currency: String,
    val balance: BigDecimal,
    val equity: BigDecimal,
    val marginFree: BigDecimal,
    val asOf: Instant
)

data class OrderIntent(
    val brokerSymbol: String,
    val side: TradeSide,
    val volumeLots: BigDecimal,
    val stopLoss: BigDecimal?,
    val takeProfit: BigDecimal?,
    val estimatedRiskAmount: BigDecimal,
    val idempotencyKey: String
)

data class RiskPolicy(
    val maxRiskPerTrade: BigDecimal,
    val maxDailyLoss: BigDecimal,
    val maxVolumeLots: BigDecimal,
    val maxOpenPositions: Int,
    val allowedSymbols: Set<String>
)

data class RiskCheckResult(
    val approved: Boolean,
    val reason: String? = null
)
