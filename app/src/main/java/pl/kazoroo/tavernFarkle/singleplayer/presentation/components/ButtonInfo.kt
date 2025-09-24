package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

import androidx.compose.ui.Modifier

data class ButtonInfo(
    val text: String,
    val modifier: Modifier = Modifier,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)