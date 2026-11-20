package com.gptradingpro.app.domain

import java.math.BigDecimal
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RiskPolicyValidatorTest {
    private val policy = RiskPolicy(
        maxRiskPerTrade = BigDecimal("25"),
        maxDailyLoss = BigDecimal("100"),
        maxVolumeLots = BigDecimal("1.5"),
        maxOpenPositions = 3,
        allowedSymbols = setOf("XAUUSD", "EURUSD")
    )

    private fun intent(
        symbol: String = "XAUUSD",
        volume: String = "0.10",
        risk: String = "10",
        key: String = "request-001"
    ) = OrderIntent(
        brokerSymbol = symbol,
        side = TradeSide.BUY,
        volumeLots = BigDecimal(volume),
        stopLoss = BigDecimal("2300.00"),
        takeProfit = BigDecimal("2310.00"),
        estimatedRiskAmount = BigDecimal(risk),
        idempotencyKey = key
    )

    @Test fun approvesRequestWithinConfiguredLimits() {
        val result = RiskPolicyValidator.validate(
            intent(), policy, BigDecimal("20"), 1
        )
        assertTrue(result.approved)
    }

    @Test fun rejectsSymbolOutsideAllowList() {
        val result = RiskPolicyValidator.validate(
            intent(symbol = "UNKNOWN"), policy, BigDecimal("0"), 0
        )
        assertFalse(result.approved)
    }

    @Test fun rejectsVolumeAboveMaximum() {
        val result = RiskPolicyValidator.validate(
            intent(volume = "2.0"), policy, BigDecimal("0"), 0
        )
        assertFalse(result.approved)
    }

    @Test fun rejectsOrderThatWouldBreachDailyLossLimit() {
        val result = RiskPolicyValidator.validate(
            intent(risk = "20"), policy, BigDecimal("90"), 0
        )
        assertFalse(result.approved)
    }

    @Test fun rejectsWhenPositionLimitReached() {
        val result = RiskPolicyValidator.validate(
            intent(), policy, BigDecimal("0"), 3
        )
        assertFalse(result.approved)
    }
}
