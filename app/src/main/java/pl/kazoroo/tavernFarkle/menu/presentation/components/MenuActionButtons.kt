package pl.kazoroo.tavernFarkle.menu.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.menu.presentation.RevealableKeys
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType

@Composable
fun BoxScope.MenuActionButtons(
    navController: NavController,
    revealState: RevealState
) {
    var isHelpDialogVisible by remember { mutableStateOf(false) }
    if (isHelpDialogVisible) {
        HowToPlayDialog(onCloseClick = { isHelpDialogVisible = false })
    }

    Column(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(dimensionResource(R.dimen.small_padding))
    ) {
        val padding = dimensionResource(R.dimen.small_padding)

        ActionIconButton(
            painterIcon = painterResource(R.drawable.settings_24dp_white),
            onClick = {
                navController.navigate(Screen.SettingsScreen.route)
                SoundPlayer.playSound(SoundType.CLICK)
            },
            modifier = Modifier
        )

        Spacer(Modifier.height(15.dp))

        ActionIconButton(
            painterIcon = painterResource(R.drawable.help_outline_24),
            onClick = { isHelpDialogVisible = !isHelpDialogVisible },
            modifier = Modifier
                .padding(end = padding, bottom = padding)
                .revealable(key = RevealableKeys.HowToPlay, state = revealState)
        )
    }
}

@Composable
fun ActionIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painterIcon: Painter? = null,
    onClick: () -> Unit,
) {
    val size = dimensionResource(R.dimen.icon_button_size)
    val corner = dimensionResource(R.dimen.rounded_corner)

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .dropShadow(
                shape = RoundedCornerShape(corner),
                shadow = Shadow(
                    color = Color(0x80000000),
                    radius = 10.dp,
                    offset = DpOffset(2.dp, 4.dp)
                )
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.DarkGray, RoundedCornerShape(100))
        ) {
            Image(
                painter = painterResource(R.drawable.circle_glossy_button),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            icon?.let { Icon(it, contentDescription = null, tint = Color.Black) }
            painterIcon?.let { Icon(it, contentDescription = null, tint = Color.Black) }
        }
    }
}