package com.jetbrains.inlock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val InLockBlue = Color(0xFF1E88E5)
val InLockDarkBlue = Color(0xFF1565C0)
val InLockLightBlue = Color(0xFF90CAF9)

val inLockLightColorScheme = lightColorScheme(
    primary = InLockBlue,
    secondary = InLockLightBlue,
    tertiary = InLockDarkBlue
)

val inLockDarkColorScheme = darkColorScheme(
    primary = InLockBlue,
    secondary = InLockLightBlue,
    tertiary = InLockDarkBlue
)

@Composable
fun InLockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) inLockDarkColorScheme else inLockLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}