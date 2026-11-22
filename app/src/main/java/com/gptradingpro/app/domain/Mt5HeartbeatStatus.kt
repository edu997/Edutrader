package com.gptradingpro.app.domain

import java.time.Duration
import java.time.Instant

/** Read-only connection information received from the trusted MT5 gateway. */
data class Mt5Heartbeat(
    val terminalAlias: String,
    val accountLogin: String,
    val brokerServer: String,
    val currency: String,
    val balance: String,
    val equity: String,
    val freeMargin: String,
    val terminalConnected: Boolean,
    val receivedAt: Instant
)

enum class Mt5LinkState { NOT_CONFIGURED, OFFLINE, CONNECTED, STALE }

data class Mt5ConnectionStatus(
    val state: Mt5LinkState,
    val heartbeat: Mt5Heartbeat? = null,
    val detail: String
) {
    companion object {
        fun evaluate(
            heartbeat: Mt5Heartbeat?,
            now: Instant,
            staleAfter: Duration = Duration.ofSeconds(90)
        ): Mt5ConnectionStatus {
            if (heartbeat == null) return Mt5ConnectionStatus(
                Mt5LinkState.NOT_CONFIGURED,
                detail = "Esperando el primer heartbeat autenticado."
            )
            if (!heartbeat.terminalConnected) return Mt5ConnectionStatus(
                Mt5LinkState.OFFLINE, heartbeat,
                "El terminal MT5 informa que está desconectado."
            )
            val age = Duration.between(heartbeat.receivedAt, now)
            if (age.isNegative || age > staleAfter) return Mt5ConnectionStatus(
                Mt5LinkState.STALE, heartbeat,
                "Datos desactualizados. Se requiere un heartbeat reciente."
            )
            return Mt5ConnectionStatus(
                Mt5LinkState.CONNECTED, heartbeat,
                "Terminal conectado. Integración en modo de solo lectura."
            )
        }
    }
}