package com.gptradingpro.app.domain

import java.math.BigDecimal

/**
 * Client-side preflight only. The backend and the MT5 EA must independently
 * repeat every risk check before any order can be sent to a broker.
 */
object RiskPolicyValidator {
    fun validate(
        intent: OrderIntent,
        policy: RiskPolicy,
        currentDailyLoss: BigDecimal,
        openPositionCount: Int
    ): RiskCheckResult {
        if (intent.brokerSymbol !in policy.allowedSymbols) {
            return RiskCheckResult(false, "El símbolo no está autorizado.")
        }
        if (intent.volumeLots <= BigDecimal.ZERO) {
            return RiskCheckResult(false, "El volumen debe ser mayor que cero.")
        }
        if (intent.volumeLots > policy.maxVolumeLots) {
            return RiskCheckResult(false, "El volumen supera el máximo permitido.")
        }
        if (intent.estimatedRiskAmount < BigDecimal.ZERO ||
            intent.estimatedRiskAmount > policy.maxRiskPerTrade
        ) {
            return RiskCheckResult(false, "El riesgo estimado por operación supera el límite.")
        }
        if (openPositionCount >= policy.maxOpenPositions) {
            return RiskCheckResult(false, "Se alcanzó el máximo de posiciones abiertas.")
        }
        if (currentDailyLoss < BigDecimal.ZERO ||
            currentDailyLoss + intent.estimatedRiskAmount > policy.maxDailyLoss
        ) {
            return RiskCheckResult(false, "La operación podría superar la pérdida diaria máxima.")
        }
        if (intent.idempotencyKey.isBlank()) {
            return RiskCheckResult(false, "Falta la clave única de la solicitud.")
        }
        return RiskCheckResult(true)
    }
}
