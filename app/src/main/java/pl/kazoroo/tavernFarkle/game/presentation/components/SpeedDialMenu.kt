package pl.kazoroo.tavernFarkle.game.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.HowToPlayDialog
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundType

@Composable
fun SpeedDialMenu(
    modifier: Modifier,
    navController: NavController,
    restricted: Boolean = false
) {
    var isHelpDialogVisible by remember { mutableStateOf(false) }
    val speedDialData = buildList {
        add(
            SpeedDialData(
                painterResource = R.drawable.help_outline_24,
                label = stringResource(R.string.tutorial),
                onClick = { isHelpDialogVisible = !isHelpDialogVisible }
            )
        )
        if (!restricted) {
            add(
                SpeedDialData(
                    painter = rememberVectorPainter(Icons.Outlined.Settings),
                    label = stringResource(R.string.settings),
                    onClick = {
                        navController.navigate(Screen.SettingsScreen.route)
                        SoundPlayer.playSound(SoundType.CLICK)
                    }
                )
            )
        }
    }

    if(isHelpDialogVisible) {
        HowToPlayDialog(
            onCloseClick = { isHelpDialogVisible = false }
        )
    }

    SpeedDial(
        speedDialData = speedDialData,
        modifier = modifier,
        fabBackgroundColor = Color.White,
        speedDialBackgroundColor = MaterialTheme.colorScheme.primary,
        speedDialContentColor = Color.White,
        showLabels = true
    )
}