package pl.kazoroo.tavernFarkle.menu.presentation.components

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.svenjacobs.reveal.RevealScope
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.menu.presentation.RevealableKeys
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.ButtonInfo
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.DiceButton

@Composable
fun RevealScope.MenuNavigationButtons(
    navController: NavController,
    playWithComputerOnClick: () -> Unit,
    playOnlineOnClick: () -> Unit
) {
    val activity = LocalActivity.current
    val buttonsModifier: Modifier = Modifier
        .padding(bottom = dimensionResource(R.dimen.medium_padding))
        .height(dimensionResource(R.dimen.menu_button_height))
        .width(dimensionResource(R.dimen.menu_button_width))

    Column {
        DiceButton(
            buttonInfo = ButtonInfo(
                text = stringResource(R.string.play_with_computer),
                modifier = buttonsModifier
                    .testTag("Play with AI button"),
                onClick = playWithComputerOnClick
            ),
            modifier = buttonsModifier
        )

        DiceButton(
            buttonInfo = ButtonInfo(
                text = stringResource(R.string.play_online),
                modifier = buttonsModifier
                    .testTag("Play online button"),
                onClick = playOnlineOnClick
            ),
            modifier = buttonsModifier
        )

        DiceButton(
            buttonInfo = ButtonInfo(
                text = stringResource(R.string.shop),
                modifier = buttonsModifier
                    .testTag("Shop"),
                onClick = {
                    navController.navigate(Screen.ShopScreen.route)
                    SoundPlayer.playSound(SoundType.CLICK)
                }
            ),
            modifier = buttonsModifier.revealable(key = RevealableKeys.ShopButton)
        )

        DiceButton(
            buttonInfo = ButtonInfo(
                text = stringResource(R.string.inventory),
                modifier = buttonsModifier
                    .testTag("Inventory"),
                onClick = {
                    navController.navigate(Screen.InventoryScreen.route)
                    SoundPlayer.playSound(SoundType.CLICK)
                }
            ),
            modifier = buttonsModifier.revealable(key = RevealableKeys.InventoryButton)
        )

        Spacer(Modifier.weight(1f))

        DiceButton(
            buttonInfo = ButtonInfo(
                text = stringResource(R.string.exit),
                onClick = {
                    activity?.finish()
                    SoundPlayer.playSound(SoundType.CLICK)
                }
            ),
            modifier = buttonsModifier
                .testTag("Exit button")
        )
    }
}