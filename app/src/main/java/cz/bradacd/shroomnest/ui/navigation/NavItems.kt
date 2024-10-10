package cz.bradacd.shroomnest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem (
    val label: String,
    val icon: ImageVector,
    val route: String,
)

val navItems = listOf(
    NavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = Screens.HomeScreen.name
    ),
    NavItem(
        label = "Humidity",
        icon = Icons.Default.WaterDrop,
        route = Screens.HumidityScreen.name
    ),
    NavItem(
        label = "Ventilation",
        icon = Icons.Default.WindPower,
        route = Screens.VentilationScreen.name
    ),
    NavItem(
        label = "Settings",
        icon = Icons.Default.Settings,
        route = Screens.SettingsScreen.name
    ),
)