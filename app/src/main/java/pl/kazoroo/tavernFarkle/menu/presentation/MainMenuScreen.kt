package pl.kazoroo.tavernFarkle.menu.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.svenjacobs.reveal.Reveal
import com.svenjacobs.reveal.RevealCanvasState
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.rememberRevealState
import kotlinx.coroutines.delay
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.core.presentation.components.BettingDialog
import pl.kazoroo.tavernFarkle.core.presentation.components.CoinAmountIndicator
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.menu.presentation.components.AppTitleText
import pl.kazoroo.tavernFarkle.menu.presentation.components.CustomRhombusShadow
import pl.kazoroo.tavernFarkle.menu.presentation.components.MenuActionButtons
import pl.kazoroo.tavernFarkle.menu.presentation.components.MenuNavigationButtons
import pl.kazoroo.tavernFarkle.menu.presentation.components.RevealOverlayContent
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel

private const val ONBOARDING_INITIAL_DELAY_MS = 1500L

@Composable
fun MainMenuScreen(
    navController: NavController,
    coinsViewModel: CoinsViewModel,
    mainMenuViewModel: MainMenuViewModel,
    inventoryViewModel: InventoryViewModel,
    revealCanvasState: RevealCanvasState
) {
    var isBettingDialogVisible by remember { mutableStateOf(false) }
    val revealState = rememberRevealState()
    val isFirstLaunch = mainMenuViewModel.isFirstLaunch.collectAsState().value
    val onboardingStage = mainMenuViewModel.onboardingStage.collectAsState().value

    LaunchedEffect(key1 = onboardingStage) {
        if(isFirstLaunch) {
            when(onboardingStage) {
                RevealableKeys.Welcome.ordinal -> {
                    delay(ONBOARDING_INITIAL_DELAY_MS)
                    revealState.reveal(RevealableKeys.Welcome)
                }
                RevealableKeys.Skucha.ordinal -> {
                    revealState.reveal(RevealableKeys.Skucha)
                }
                RevealableKeys.SinglePlayer.ordinal -> {
                    revealState.reveal(RevealableKeys.SinglePlayer)
                }
                RevealableKeys.MultiPlayer.ordinal -> {
                    revealState.reveal(RevealableKeys.MultiPlayer)
                }
                RevealableKeys.HowToPlay.ordinal -> {
                    revealState.reveal(RevealableKeys.HowToPlay)
                }
                RevealableKeys.ShopButton.ordinal -> {
                    revealState.reveal(RevealableKeys.ShopButton)
                }
                RevealableKeys.InventoryButton.ordinal -> {
                    revealState.reveal(RevealableKeys.InventoryButton)
                }
                RevealableKeys.Hide.ordinal -> {
                    revealState.hide()
                    mainMenuViewModel.finishOnboarding()
                }
            }
        }
    }

    MainMenuContent(
        coinsViewModel = coinsViewModel,
        navController = navController,
        revealCanvasState = revealCanvasState,
        playWithComputerOnClick = { isBettingDialogVisible = true },
        revealState = revealState,
        onboardingOnClick = { mainMenuViewModel.nextOnboardingStage() }
    )

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

                AppTitleText(revealState)

                MenuNavigationButtons(
                    playWithComputerOnClick = playWithComputerOnClick,
                    navController = navController
                )
            }

            MenuActionButtons(navController, revealState)
        }
    }
}
