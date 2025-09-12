package pl.kazoroo.tavernFarkle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun DicesTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = lightColorScheme(
        primary = Gold,
        secondary = LightRed,
        tertiary = Teal200
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = provideTypography(),
        content = content
    )
}