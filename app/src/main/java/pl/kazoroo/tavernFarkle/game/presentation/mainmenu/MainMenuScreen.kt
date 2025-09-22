package pl.kazoroo.tavernFarkle.game.presentation.mainmenu

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.svenjacobs.reveal.Reveal
import com.svenjacobs.reveal.RevealCanvas
import com.svenjacobs.reveal.RevealCanvasState
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.rememberRevealCanvasState
import com.svenjacobs.reveal.rememberRevealState
import kotlinx.coroutines.delay
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.core.presentation.components.CoinAmountIndicator
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.AppTitleText
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.BettingDialog
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.CustomRhombusShadow
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.MenuActionButtons
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.MenuNavigationButtons
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.RevealOverlayContent
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundType
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel

private const val ONBOARDING_INITIAL_DELAY_MS = 1500L

@Composable
fun MainMenuScreen(
    navController: NavController,
    coinsViewModel: CoinsViewModel,
    mainMenuViewModel: MainMenuViewModel,
    inventoryViewModel: InventoryViewModel
) {
    var isBettingDialogVisible by remember { mutableStateOf(false) }
    val revealCanvasState = rememberRevealCanvasState()
    val revealState = rememberRevealState()
    val isFirstLaunch = mainMenuViewModel.isFirstLaunch.collectAsState().value
    val onboardingStage = mainMenuViewModel.onboardingStage.collectAsState().value

    LaunchedEffect(key1 = onboardingStage) {
        if(isFirstLaunch) {
            when(onboardingStage) {
                RevealableKeys.SpeedDialMenu.ordinal -> {
                    delay(ONBOARDING_INITIAL_DELAY_MS)
                    revealState.reveal(RevealableKeys.SpeedDialMenu)
                }
                RevealableKeys.ShopButton.ordinal -> revealState.reveal(RevealableKeys.ShopButton)
                RevealableKeys.InventoryButton.ordinal -> revealState.reveal(RevealableKeys.InventoryButton)
                RevealableKeys.Hide.ordinal -> {
                    revealState.hide()
                    mainMenuViewModel.finishOnboarding()
                }
            }
        }
    }

    RevealCanvas(
        modifier = Modifier.fillMaxSize(),
        revealCanvasState = revealCanvasState,
    ) {
        MainMenuContent(
            coinsViewModel = coinsViewModel,
            navController = navController,
            revealCanvasState = revealCanvasState,
            playWithComputerOnClick = { isBettingDialogVisible = true },
            revealState = revealState,
            onboardingOnClick = { mainMenuViewModel.nextOnboardingStage() }
        )
    }

    if(isBettingDialogVisible) {
        BettingDialog(
            onCloseClick = {
                isBettingDialogVisible = false
            },
            onClick = { betAmount ->
                mainMenuViewModel.startNewGame(
                    betAmount = betAmount.toInt(),
                    userSpecialDiceNames = inventoryViewModel.getSelectedSpecialDiceNames()
                )
                SoundPlayer.playSound(SoundType.CLICK)
                navController.navigate(Screen.GameScreen.withArgs(false))
                coinsViewModel.setBetValue(betAmount)
            },
            coinsAmount = coinsViewModel.coinsAmount.collectAsState().value.toInt()
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun MainMenuContent(
    coinsViewModel: CoinsViewModel,
    navController: NavController,
    revealCanvasState: RevealCanvasState,
    playWithComputerOnClick: () -> Unit,
    revealState: RevealState,
    onboardingOnClick: (Any) -> Unit
) {
    val imageSize = (LocalConfiguration.current.screenWidthDp / 2).dp

    BackgroundImage()

    Reveal(
        revealCanvasState = revealCanvasState,
        revealState = revealState,
        onRevealableClick = onboardingOnClick,
        onOverlayClick = onboardingOnClick,
        overlayContent = { key -> RevealOverlayContent(key) },
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                    .align(Alignment.TopCenter)
            ) {
                Box {
                    CustomRhombusShadow(imageSize)

                    Image(
                        painter = painterResource(id = R.drawable.dice_1),
                        contentDescription = "Dice",
                        modifier = Modifier.size(imageSize)
                    )
                }

                AppTitleText()

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.large_padding)))

                MenuNavigationButtons(
                    playWithComputerOnClick = playWithComputerOnClick,
                    playOnlineOnClick = { navController.navigate(Screen.LobbyScreen.route) },
                    navController = navController
                )
            }

            MenuActionButtons(navController)
        }
    }
}
