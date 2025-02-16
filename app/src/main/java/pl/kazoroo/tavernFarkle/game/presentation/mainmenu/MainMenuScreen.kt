package pl.kazoroo.tavernFarkle.game.presentation.mainmenu

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.CoinAmountIndicator
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.game.presentation.components.ButtonInfo
import pl.kazoroo.tavernFarkle.game.presentation.components.DiceButton
import pl.kazoroo.tavernFarkle.game.presentation.components.SpeedDialMenu
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.AppTitleText
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.BettingDialog
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundType

@Composable
fun MainMenuScreen(navController: NavController, coinsViewModel: CoinsViewModel) {
    val activity = LocalActivity.current
    val buttonsModifier: Modifier = Modifier
        .padding(bottom = dimensionResource(R.dimen.medium_padding))
        .height(dimensionResource(R.dimen.menu_button_height))
        .width(dimensionResource(R.dimen.menu_button_width))
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageSize = (screenWidth / 1.6f)
    var isBettingDialogVisible by remember { mutableStateOf(false) }
    val buttons = listOf(
        ButtonInfo(
            text = stringResource(R.string.play_with_computer),
            modifier = buttonsModifier
                .testTag("Play with AI button")
        ) {
            isBettingDialogVisible = true
        },

        ButtonInfo(
            text = stringResource(R.string.shop),
            modifier = buttonsModifier
                .testTag("Shop")
        ) {
            navController.navigate(Screen.ShopScreen.route)
        },

        ButtonInfo(
            text = stringResource(R.string.inventory),
            modifier = buttonsModifier
                .testTag("Inventory")
        ) {
            navController.navigate(Screen.InventoryScreen.route)
        }
    )

    if(isBettingDialogVisible) {
        BettingDialog(
            onCloseClick = {
                isBettingDialogVisible = false
            },
            onClick = {
                SoundPlayer.playSound(SoundType.CLICK)
                navController.navigate(Screen.GameScreen.withArgs())
            },
            coinsViewModel = coinsViewModel
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.wooden_background_texture),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        SpeedDialMenu(modifier = Modifier.align(Alignment.TopEnd))


        CoinAmountIndicator(
            coinsAmount = coinsViewModel.coinsAmount.collectAsState().value,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .testTag("Main menu screen")
                .padding(top = dimensionResource(R.dimen.small_padding))
        ) {
            Image(
                painter = painterResource(id = R.drawable.dice_1),
                contentDescription = "Dice",
                modifier = Modifier.size(imageSize)
            )

            AppTitleText()

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.large_padding)))

            buttons.forEach { buttonInfo ->
                DiceButton(
                    buttonInfo = buttonInfo,
                    modifier = buttonsModifier
                )
            }

            Spacer(modifier = Modifier.weight(0.9f))

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

            val context = LocalContext.current
            val versionName = remember {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            }

            Text(
                text = "$versionName",
                modifier = Modifier.padding(dimensionResource(R.dimen.small_padding)),
                color = Color.Gray
            )
        }
    }
}