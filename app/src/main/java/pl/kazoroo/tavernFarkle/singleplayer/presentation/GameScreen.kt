package pl.kazoroo.tavernFarkle.singleplayer.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.svenjacobs.reveal.Reveal
import com.svenjacobs.reveal.RevealCanvasState
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.rememberRevealState
import kotlinx.coroutines.delay
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.model.TableData
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.menu.presentation.components.ActionIconButton
import pl.kazoroo.tavernFarkle.menu.presentation.components.HowToPlayDialog
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.PlayerStatus
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.ButtonInfo
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.ExitDialog
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.GameButtons
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.GameResultAndSkuchaDialog
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.GameRevealOverlayContent
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.InteractiveDiceLayout
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.PointsTable
import pl.kazoroo.tavernFarkle.ui.theme.DarkRed

private const val ONBOARDING_INITIAL_DELAY_MS = 2000L

@Composable
fun GameScreen(
    navController: NavHostController,
    viewModel: GameViewModel,
    coinsViewModel: CoinsViewModel,
    revealCanvasState: RevealCanvasState
) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()

    val revealState = rememberRevealState()
    val isFirstLaunch = viewModel.isFirstLaunch.collectAsState().value
    val onboardingStage = viewModel.onboardingStage.collectAsState().value

    val myPlayerIndex = viewModel.myPlayerIndex
    val showExitDialog = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var playerLeftGameThroughDialog by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if(!playerLeftGameThroughDialog && state.players.count() == 2) {
                        viewModel.updatePlayerState(PlayerStatus.PAUSED, context = context)
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    val player = viewModel.gameState.value.players[myPlayerIndex]

                    if(player.statusTimestamp < System.currentTimeMillis() - 30_000 && player.status == PlayerStatus.PAUSED) {
                        coinsViewModel.handleGameEndRewards(false)

                        navController.navigateUp()

                        return@LifecycleEventObserver
                    }

                    viewModel.updatePlayerState(PlayerStatus.IN_GAME, context = context)
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler {
        showExitDialog.value = true
    }

    LaunchedEffect(true) {
        viewModel.onGameEnd(
            navController,
            handleGameEndRewards = { isWin -> coinsViewModel.handleGameEndRewards(isWin) }
        )
        viewModel.observePlayerStatus(navController) {
            coinsViewModel.handleGameEndRewards(true)
        }
    }

    if(showExitDialog.value) {
        ExitDialog(
            onDismissClick = { showExitDialog.value = false },
            onQuitClick = {
                playerLeftGameThroughDialog = true
                showExitDialog.value = false
                viewModel.onQuit(
                    takeBet = {
                        coinsViewModel.takeCoinsFromWallet(amount = state.betAmount)
                    }
                )
                navController.navigateUp()
            }
        )
    }

    LaunchedEffect(key1 = onboardingStage) {
        if(!isFirstLaunch || viewModel.isMultiplayer)  return@LaunchedEffect
        while(viewModel.isDiceAnimating.value) {
            delay(50)
        }

        when(onboardingStage) {
            GameRevealableKeys.ScoringDice.ordinal -> {
                delay(400)
                revealState.reveal(GameRevealableKeys.ScoringDice)
            }
            GameRevealableKeys.ScoreButton.ordinal -> {
                delay(400)
                revealState.reveal(GameRevealableKeys.ScoreButton)
            }
            GameRevealableKeys.ThreeOfKindFirstDice.ordinal -> {
                delay(ONBOARDING_INITIAL_DELAY_MS)
                revealState.reveal(GameRevealableKeys.ThreeOfKindFirstDice)
            }
            GameRevealableKeys.ThreeOfKindSecondDice.ordinal -> {
                delay(400)
                revealState.reveal(GameRevealableKeys.ThreeOfKindSecondDice)
            }
            GameRevealableKeys.ThreeOfKindThirdDice.ordinal -> {
                delay(400)
                revealState.reveal(GameRevealableKeys.ThreeOfKindThirdDice)
            }
            GameRevealableKeys.PassButton.ordinal -> {
                delay(400)
                revealState.reveal(GameRevealableKeys.PassButton)
            }
            GameRevealableKeys.Hide.ordinal -> {
                revealState.hide()
                viewModel.finishOnboarding()
            }
        }
    }

    GameContent(
        viewModel = viewModel,
        coinsViewModel = coinsViewModel,
        revealCanvasState = revealCanvasState,
        revealState = revealState,
        state = state,
        myPlayerIndex = myPlayerIndex
    )
}

@Composable
fun GameContent(
    viewModel: GameViewModel,
    coinsViewModel: CoinsViewModel,
    revealCanvasState: RevealCanvasState,
    revealState: RevealState,
    state: GameState,
    myPlayerIndex: Int
) {
    val isOpponentTurn = viewModel.isOpponentTurn.collectAsState().value
    val currentPlayerIndex = state.getCurrentPlayerIndex()
    val isGameResultDialogVisible = viewModel.showGameEndDialog.collectAsState().value
    val isSkuchaDialogVisible = viewModel.showSkuchaDialog.collectAsState().value
    val opponentPlayerIndex = viewModel.opponentPlayerIndex.collectAsState().value
    val selectedPoints = state.players[myPlayerIndex].selectedPoints
    val tableData = listOf(
        TableData(
            pointsType = stringResource(R.string.total),
            yourPoints = state.players[myPlayerIndex].totalPoints.toString(),
            opponentPoints = opponentPlayerIndex?.let { state.players[it].totalPoints.toString() } ?: "-"
        ),
        TableData(
            pointsType = stringResource(R.string.round),
            yourPoints = state.players[myPlayerIndex].roundPoints.toString(),
            opponentPoints = opponentPlayerIndex?.let { state.players[it].roundPoints.toString() } ?: "-"
        ),
        TableData(
            pointsType = stringResource(R.string.selected_forDices),
            yourPoints = selectedPoints.toString(),
            opponentPoints = opponentPlayerIndex?.let { state.players[it].selectedPoints.toString() } ?: "-"
        ),
    )
    var isHelpDialogVisible by remember { mutableStateOf(false) }
    if(isHelpDialogVisible) {
        HowToPlayDialog(
            onCloseClick = { isHelpDialogVisible = false }
        )
    }

    Reveal(
        revealCanvasState = revealCanvasState,
        revealState = revealState,
        onRevealableClick = { key ->
            when(key) {
                GameRevealableKeys.ScoringDice -> {
                    viewModel.toggleDiceSelection(4)
                    viewModel.nextOnboardingStage()
                }
                GameRevealableKeys.ScoreButton -> {
                    viewModel.onScoreAndRollAgain()
                    viewModel.nextOnboardingStage()
                }
                GameRevealableKeys.ThreeOfKindFirstDice -> {
                    viewModel.toggleDiceSelection(2)
                    viewModel.nextOnboardingStage()
                }
                GameRevealableKeys.ThreeOfKindSecondDice -> {
                    viewModel.toggleDiceSelection(3)
                    viewModel.nextOnboardingStage()
                }
                GameRevealableKeys.ThreeOfKindThirdDice -> {
                    viewModel.toggleDiceSelection(5)
                    viewModel.nextOnboardingStage()
                }
                GameRevealableKeys.PassButton -> {
                    viewModel.onPass()
                    viewModel.nextOnboardingStage()
                }
            }
        },
        overlayContent = { key -> GameRevealOverlayContent(key) },
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("Game screen")
        ) {
            BackgroundImage()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box {
                    PointsTable(
                        data = tableData,
                        isOpponentTurn = isOpponentTurn
                    )

                    ActionIconButton(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .systemBarsPadding()
                            .padding(start = dimensionResource(R.dimen.medium_padding)),
                        painterIcon = painterResource(R.drawable.help_outline_24),
                    ) {
                        isHelpDialogVisible = !isHelpDialogVisible
                    }
                }

                InteractiveDiceLayout(
                    diceState = state.players[currentPlayerIndex].diceSet,
                    diceOnClick = { index ->
                        if (!state.isSkucha) {
                            viewModel.toggleDiceSelection(index)
                        }
                    },
                    isDiceClickable = !isOpponentTurn && !state.isGameEnd,
                    isDiceAnimating = viewModel.isDiceAnimating.collectAsState().value,
                    activePlayer = state.players.count()
                )
                Spacer(modifier = Modifier.weight(1f))

                val isActionAllowed =
                    selectedPoints != 0 && !isOpponentTurn && !state.isGameEnd && state.players.size == 2
                val buttonsInfo = listOf(
                    ButtonInfo(
                        text = stringResource(id = R.string.score_and_roll_again),
                        onClick = {
                            viewModel.onScoreAndRollAgain()
                        },
                        enabled = isActionAllowed,
                    ),
                    ButtonInfo(
                        text = stringResource(id = R.string.pass),
                        onClick = {
                            viewModel.onPass()
                        },
                        enabled = isActionAllowed,
                    ),
                )

                GameButtons(
                    buttonsInfo = buttonsInfo,
                )
            }
        }

        GameDialogs(
            isSkuchaDialogVisible,
            coinsViewModel,
            isGameResultDialogVisible,
            isOpponentTurn,
            viewModel
        )
    }
}

@Composable
private fun GameDialogs(
    isSkuchaDialogVisible: Boolean,
    coinsViewModel: CoinsViewModel,
    isGameResultDialogVisible: Boolean,
    isOpponentTurn: Boolean,
    viewModel: GameViewModel
) {
    val coinsBefore = coinsViewModel.coinsAmountAfterBetting.collectAsState()
    val betValue = coinsViewModel.betValue.collectAsState()

    if (isSkuchaDialogVisible) {
        GameResultAndSkuchaDialog(
            text = "Skucha!", textColor = Color(212, 212, 212),
            extraText = null
        )
    }

    if (isGameResultDialogVisible && isOpponentTurn) {
        SoundPlayer.playSound(SoundType.FAILURE)

        GameResultAndSkuchaDialog(
            text = "Defeat",
            textColor = DarkRed,
            extraText = if (coinsBefore.value == 0) "+ 50 starter coins to continue playing"
            else "- ${betValue.value} coins"
        )
    } else if (isGameResultDialogVisible) {
        SoundPlayer.playSound(SoundType.WIN)

        GameResultAndSkuchaDialog(
            text = "Win!",
            textColor = Color.Green,
            extraText = if (coinsBefore.value == 0 && betValue.value.toInt() == 0) "+ 50 starter coins to continue playing"
            else "+ ${betValue.value} coins"
        )
    }

    if (viewModel.playerQuit) {
        SoundPlayer.playSound(SoundType.WIN)

        GameResultAndSkuchaDialog(
            text = "Win by giving up",
            textColor = Color.Green,
            extraText = if (coinsBefore.value == 0 && betValue.value.toInt() == 0) "+ 50 starter coins to continue playing"
            else "Opponent leave the game\n+${betValue.value} coins",
        )
    }

    if (viewModel.timerValue > -1) {
        GameResultAndSkuchaDialog(
            text = viewModel.timerValue.toString(),
            textColor = Color.White,
            extraText = "Waiting for opponent to return"
        )
    }
}
