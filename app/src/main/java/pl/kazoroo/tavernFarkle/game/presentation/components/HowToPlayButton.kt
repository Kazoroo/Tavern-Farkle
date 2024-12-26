package pl.kazoroo.tavernFarkle.game.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.HowToPlayDialog

@Composable
fun HowToPlayButton(modifier: Modifier, tint: Color) {
    var isHelpDialogVisible by remember { mutableStateOf(false) }

    if(isHelpDialogVisible) {
        HowToPlayDialog(
            onCloseClick = { isHelpDialogVisible = false }
        )
    }

    IconButton(
        onClick = { isHelpDialogVisible = !isHelpDialogVisible },
        modifier = modifier
            .padding(
                top = dimensionResource(R.dimen.large_padding),
                end = dimensionResource(R.dimen.small_padding),
                start = dimensionResource(R.dimen.small_padding)
            )
            .size(dimensionResource(R.dimen.icon_button_size))
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Info icon",
            tint = tint,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size))
        )
    }
}