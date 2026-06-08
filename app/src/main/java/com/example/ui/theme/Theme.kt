package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ThermalAmber,
    secondary = JadeLight,
    tertiary = GoldGlow,
    background = EspressoDark,
    surface = Color(0xFF251F33),
    onPrimary = EspressoDark,
    onSecondary = EspressoDark,
    onBackground = OffWhiteComfort,
    onSurface = OffWhiteComfort,
    surfaceVariant = Color(0xFF363046),
    onSurfaceVariant = Color(0xFFE7E0EC)
)

private val LightColorScheme = lightColorScheme(
    primary = ThermalTerra,
    secondary = JadeStone,
    tertiary = ThermalAmber,
    background = OffWhiteComfort,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = ClayWarmText,
    onSurface = ClayWarmText,
    surfaceVariant = Color(0xFFF7F2FA),
    onSurfaceVariant = ClayWarmText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false by default for our design to preserve our curated color identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
