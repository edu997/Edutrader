package com.gptradingpro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF08111F)
private val Panel = Color(0xFF111E30)
private val PanelAlt = Color(0xFF17263A)
private val Gold = Color(0xFFD6AE57)
private val Muted = Color(0xFF91A0B5)
private val Positive = Color(0xFF35C58A)
private val Negative = Color(0xFFFF6B72)
private val White = Color(0xFFF5F7FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { EdTradingProApp() }
    }
}

private enum class MainTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Default.Home),
    Markets("Mercados", Icons.Default.ShowChart),
    Trade("Operar", Icons.Default.SwapHoriz),
    Bot("Bot IA", Icons.Default.AutoAwesome),
    Journal("Diario", Icons.Default.BarChart)
}

@Composable
private fun EdTradingProApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var accountMode by remember { mutableStateOf("DEMO") }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Gold,
        secondary = Gold,
        background = Navy,
        surface = Panel,
        onPrimary = Navy,
        onBackground = White,
        onSurface = White
    )) {
        Scaffold(
            containerColor = Navy,
            bottomBar = {
                NavigationBar(containerColor = Panel, contentColor = Gold) {
                    MainTab.entries.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, fontSize = 10.sp) }
                        )
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Header(accountMode, onModeChange = { accountMode = it }) }
                item {
                    when (MainTab.entries[selectedTab]) {
                        MainTab.Home -> HomeDashboard(accountMode)
                        MainTab.Markets -> PlaceholderSection("Mercados", "Los instrumentos se cargarán desde tu cuenta MT5 cuando conectemos el terminal.")
                        MainTab.Trade -> PlaceholderSection("Operar", "El ticket BUY / SELL se habilitará primero en cuenta demo, con límites de riesgo.")
                        MainTab.Bot -> PlaceholderSection("Automatización", "Aquí verás el estado del EA, su conexión, estrategia y controles de pausa.")
                        MainTab.Journal -> PlaceholderSection("Diario de trading", "Conservaremos el diario de binarias y añadiremos un historial independiente para operaciones MT5.")
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun Header(accountMode: String, onModeChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 34.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).background(PanelAlt, RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CandlestickChart, contentDescription = null, tint = Gold, modifier = Modifier.size(25.dp))
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text("ED TRADING PRO", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, letterSpacing = 1.2.sp)
                Text("TRADING INTELLIGENCE", color = Gold, fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.NotificationsNone, contentDescription = "Notificaciones", tint = Muted, modifier = Modifier.size(25.dp))
        }
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(14.dp)).padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            listOf("DEMO", "REAL").forEach { mode ->
                val active = accountMode == mode
                Surface(
                    modifier = Modifier.weight(1f).clickable { onModeChange(mode) },
                    color = if (active) Gold else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (mode == "REAL") "CUENTA REAL · PRÓXIMAMENTE" else "CUENTA DEMO",
                        color = if (active) Navy else Muted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 11.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
        if (accountMode == "REAL") {
            Text(
                "La conexión real permanecerá bloqueada hasta configurar y validar la integración MT5.",
                color = Gold, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun HomeDashboard(accountMode: String) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("RESUMEN DE CUENTA", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(13.dp))
                Text("Balance disponible", color = Muted, fontSize = 12.sp)
                Text("—", color = White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("Esperando conexión con MetaTrader 5", color = Gold, fontSize = 11.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricTile("P/L DEL DÍA", "—", "Sin datos", Modifier.weight(1f))
                    MetricTile("CAPITAL", "—", accountMode, Modifier.weight(1f))
                }
            }
        }
        Text("ESTADO DE CONEXIÓN", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
        StatusCard("MetaTrader 5", "Terminal todavía no conectado", Icons.Default.CandlestickChart)
        StatusCard("Motor automático", "EA no detectado · modo seguro", Icons.Default.AutoAwesome)
        Text("ACCESOS RÁPIDOS", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction("Ver mercados", Icons.Default.ShowChart, Modifier.weight(1f))
            QuickAction("Plan de riesgo", Icons.Default.AccountBalanceWallet, Modifier.weight(1f))
        }
        Text(
            "Aviso: los datos y las operaciones aparecerán cuando exista una conexión autenticada. Esta versión inicial no envía órdenes.",
            color = Muted, fontSize = 10.sp, lineHeight = 15.sp
        )
    }
}

@Composable
private fun MetricTile(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier.background(PanelAlt, RoundedCornerShape(13.dp)).padding(12.dp)) {
        Text(title, color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Text(value, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, fontSize = 10.sp)
    }
}

@Composable
private fun StatusCard(title: String, detail: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(14.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(38.dp).background(PanelAlt, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(detail, color = Muted, fontSize = 11.sp)
        }
        Box(Modifier.size(8.dp).background(Gold, CircleShape))
    }
}

@Composable
private fun QuickAction(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(Panel, RoundedCornerShape(14.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Gold, modifier = Modifier.size(22.dp))
        Text(title, color = White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
private fun PlaceholderSection(title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp)).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, color = Gold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(description, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
        Text("MÓDULO EN CONSTRUCCIÓN", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}
