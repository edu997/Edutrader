package com.gptradingpro.app.domain

import java.time.Duration
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class Mt5HeartbeatStatusTest {
    private val now = Instant.parse("2026-11-21T18:00:00Z")

    private fun heartbeat(connected: Boolean = true, receivedAt: Instant = now) = Mt5Heartbeat(
        terminalAlias = "demo-vps", accountLogin = "123456", brokerServer = "Broker-Demo",
        currency = "USD", balance = "1000.00", equity = "1000.00", freeMargin = "1000.00",
        terminalConnected = connected, receivedAt = receivedAt
    )

    @Test fun missingHeartbeatIsNotConfigured() {
        assertEquals(Mt5LinkState.NOT_CONFIGURED, Mt5ConnectionStatus.evaluate(null, now).state)
    }

    @Test fun recentConnectedHeartbeatIsConnected() {
        assertEquals(Mt5LinkState.CONNECTED, Mt5ConnectionStatus.evaluate(heartbeat(), now).state)
    }

    @Test fun disconnectedTerminalIsOffline() {
        assertEquals(Mt5LinkState.OFFLINE, Mt5ConnectionStatus.evaluate(heartbeat(false), now).state)
    }

    @Test fun oldHeartbeatIsStale() {
        val old = now.minus(Duration.ofSeconds(91))
        assertEquals(Mt5LinkState.STALE, Mt5ConnectionStatus.evaluate(heartbeat(receivedAt = old), now).state)
    }

    @Test fun futureHeartbeatIsStale() {
        val future = now.plusSeconds(1)
        assertEquals(Mt5LinkState.STALE, Mt5ConnectionStatus.evaluate(heartbeat(receivedAt = future), now).state)
    }
}