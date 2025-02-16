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
import de.charlex.compose.SpeedDialData
import de.charlex.compose.SpeedDialFloatingActionButton
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.HowToPlayDialog

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SpeedDialMenu(modifier: Modifier) {
    var isHelpDialogVisible by remember { mutableStateOf(false) }

    if(isHelpDialogVisible) {
        HowToPlayDialog(
            onCloseClick = { isHelpDialogVisible = false }
        )
    }

    SpeedDialFloatingActionButton(
        speedDialData = listOf(
            SpeedDialData(
                painterResource = R.drawable.help_outline_24,
                label = "Help",
                onClick = { isHelpDialogVisible = !isHelpDialogVisible }
            ),
            SpeedDialData(
                painter = rememberVectorPainter(Icons.Outlined.Settings),
                label = "Settings",
                onClick = { }
            )
        ),
        modifier = modifier
            .padding(
                top = dimensionResource(R.dimen.large_padding),
                end = dimensionResource(R.dimen.small_padding)
            )
            .rotate(180f),
        fabBackgroundColor = Color.White,
        speedDialBackgroundColor = MaterialTheme.colorScheme.primary,
        speedDialContentColor = Color.White
    )
}
