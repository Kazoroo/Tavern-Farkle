package pl.kazoroo.tavernFarkle.game.presentation.mainmenu

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
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
    val imageSize = (LocalConfiguration.current.screenWidthDp / 2).dp
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
            SoundPlayer.playSound(SoundType.CLICK)
        },

        ButtonInfo(
            text = stringResource(R.string.inventory),
            modifier = buttonsModifier
                .testTag("Inventory")
        ) {
            navController.navigate(Screen.InventoryScreen.route)
            SoundPlayer.playSound(SoundType.CLICK)
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

    BackgroundImage()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        CoinAmountIndicator(
            coinsAmount = coinsViewModel.coinsAmount.collectAsState().value,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .testTag("Main menu screen")
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
        }

        SpeedDialMenu(
            navController = navController,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }

    val isFirstLaunch = false

    if(isFirstLaunch) {
        OnboardingOverlay()
    }
}

@Composable
fun OnboardingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xD5232121))
            .systemBarsPadding(),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp, end = 30.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.swirling_arrow_white),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp),
            )

            Text(
                text = "Here you can read the rules",
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.large_padding))
                    .width(150.dp)
                    .offset(y = (-50).dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.padding(top = 350.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "In shop you buy special dice, that gives you advantage",
                modifier = Modifier
                    .width(230.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Image(
                painter = painterResource(R.drawable.top_to_bottom_arrow_white),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp),
            )
        }

        Row(
            modifier = Modifier.padding(top = 550.dp, end = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.bottom_to_top_arrow_arrow),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 10.dp),
            )

            Text(
                text = "In inventory you select dice you want to use in game",
                modifier = Modifier
                    .width(230.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(dimensionResource(R.dimen.large_padding))
                .width(200.dp),
            contentPadding = PaddingValues(15.dp),
            shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner))
        ) {
            Text("Continue")
        }
    }
}