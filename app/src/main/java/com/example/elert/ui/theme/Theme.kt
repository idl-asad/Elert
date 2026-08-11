package com.example.elert.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ElertDarkColorScheme = darkColorScheme(
    primary = ElertPrimary,
    onPrimary = ElertOnPrimary,
    primaryContainer = ElertPrimaryContainer,
    onPrimaryContainer = ElertOnPrimaryContainer,
    secondary = ElertSecondary,
    onSecondary = ElertOnSecondary,
    secondaryContainer = ElertSecondaryContainer,
    onSecondaryContainer = ElertOnSecondaryContainer,
    tertiary = ElertTertiary,
    onTertiary = ElertOnTertiary,
    tertiaryContainer = ElertTertiaryContainer,
    background = ElertBackground,
    onBackground = ElertOnBackground,
    surface = ElertSurface,
    onSurface = ElertOnSurface,
    surfaceVariant = ElertSurfaceVariant,
    onSurfaceVariant = ElertOnSurfaceVariant,
    surfaceContainerLowest = ElertSurfaceContainerLowest,
    surfaceContainerLow = ElertSurfaceContainerLow,
    surfaceContainer = ElertSurfaceContainer,
    surfaceContainerHigh = ElertSurfaceContainerHigh,
    surfaceContainerHighest = ElertSurfaceContainerHighest,
    surfaceDim = ElertSurfaceDim,
    surfaceBright = ElertSurfaceBright,
    outline = ElertOutline,
    outlineVariant = ElertOutlineVariant,
    error = ElertError,
    onError = ElertOnError,
    errorContainer = ElertErrorContainer,
    onErrorContainer = ElertOnErrorContainer
)

private val ElertLightColorScheme = lightColorScheme(
    primary = ElertPrimaryContainer,
    onPrimary = ElertOnPrimaryContainer,
    primaryContainer = ElertPrimary,
    onPrimaryContainer = ElertOnPrimary,
    secondary = ElertSecondaryContainer,
    onSecondary = ElertOnSecondaryContainer,
    secondaryContainer = ElertSecondary,
    onSecondaryContainer = ElertOnSecondary,
    tertiary = ElertTertiaryContainer,
    onTertiary = ElertOnTertiary,
    tertiaryContainer = ElertTertiary,
    onTertiaryContainer = ElertOnTertiary,
    background = ElertOnBackground,
    onBackground = ElertBackground,
    surface = ElertOnBackground,
    onSurface = ElertBackground,
    surfaceVariant = ElertTertiary,
    onSurfaceVariant = ElertOnTertiary,
    surfaceContainerLowest = ElertOnBackground,
    surfaceContainerLow = ElertTertiary,
    surfaceContainer = ElertTertiaryContainer,
    surfaceContainerHigh = ElertSecondary,
    surfaceContainerHighest = ElertSecondaryContainer,
    outline = ElertOutline,
    outlineVariant = ElertOutlineVariant,
    error = ElertErrorContainer,
    onError = ElertOnErrorContainer,
    errorContainer = ElertError,
    onErrorContainer = ElertOnError
)

@Composable
fun ElertTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> ElertDarkColorScheme
        else -> ElertLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode && view.context is Activity) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ElertBackground.toArgb()
            window.navigationBarColor = ElertSurfaceContainerLowest.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
