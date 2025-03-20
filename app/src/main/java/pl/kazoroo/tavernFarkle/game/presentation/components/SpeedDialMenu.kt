package pl.kazoroo.tavernFarkle.game.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import de.charlex.compose.SpeedDialData
import de.charlex.compose.SpeedDialFloatingActionButton
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.HowToPlayDialog
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundType

@OptIn(ExperimentalMaterialApi::class)
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

    SpeedDialFloatingActionButton(
        speedDialData = speedDialData,
        modifier = modifier
            .padding(
                end = dimensionResource(R.dimen.small_padding),
                top = dimensionResource(R.dimen.small_padding)
            )
            .rotate(180f),
        fabBackgroundColor = Color.White,
        speedDialBackgroundColor = MaterialTheme.colorScheme.primary,
        speedDialContentColor = Color.White
    )
}