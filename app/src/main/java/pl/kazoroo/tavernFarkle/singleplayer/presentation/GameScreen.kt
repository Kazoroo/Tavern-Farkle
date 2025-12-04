package pl.kazoroo.tavernFarkle.singleplayer.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import pl.kazoroo.tavernFarkle.R
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
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.InteractiveDiceLayout
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.PointsTable
import pl.kazoroo.tavernFarkle.ui.theme.DarkRed

@Composable
fun GameScreen(
    navController: NavHostController,
    viewModel: GameViewModel,
    coinsViewModel: CoinsViewModel
) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()

    val myPlayerIndex = viewModel.myPlayerIndex
    val opponentPlayerIndex = viewModel.opponentPlayerIndex.collectAsState().value
    val currentPlayerIndex = state.getCurrentPlayerIndex()
    val isOpponentTurn = viewModel.isOpponentTurn.collectAsState().value
    val selectedPoints = state.players[myPlayerIndex].selectedPoints
    val isGameResultDialogVisible = viewModel.showGameEndDialog.collectAsState().value
    val isSkuchaDialogVisible = viewModel.showSkuchaDialog.collectAsState().value

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
    val showExitDialog = remember { mutableStateOf(false) }
    var isHelpDialogVisible by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var playerLeftGame by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if(!playerLeftGame && state.players.count() == 2) {
                        viewModel.updatePlayerState(PlayerStatus.PAUSED, context = context)
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    val player = viewModel.gameState.value.players[myPlayerIndex]

                    if(player.statusTimestamp < System.currentTimeMillis() - 30_000 && player.status == PlayerStatus.PAUSED) {
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

    if(isHelpDialogVisible) {
        HowToPlayDialog(
            onCloseClick = { isHelpDialogVisible = false }
        )
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
            coinsViewModel.addBetCoinsToTotalCoinsAmount()
        }
    }

    if(showExitDialog.value) {
        ExitDialog(
            onDismissClick = { showExitDialog.value = false },
            onQuitClick = {
                playerLeftGame = true
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
                    if(!state.isSkucha) {
                        viewModel.toggleDiceSelection(index)
                    }
                },
                isDiceClickable = !isOpponentTurn && !state.isGameEnd,
                isDiceAnimating = viewModel.isDiceAnimating.collectAsState().value,
                activePlayer = state.players.count()
            )
            Spacer(modifier = Modifier.weight(1f))

            val isActionAllowed = selectedPoints != 0 && !isOpponentTurn && !state.isGameEnd && state.players.size == 2
            val buttonsInfo = listOf(
                ButtonInfo(
                    text = stringResource(id = R.string.score_and_roll_again),
                    onClick = {
                        viewModel.onScoreAndRollAgain()
                    },
                    enabled = isActionAllowed
                ),
                ButtonInfo(
                    text = stringResource(id = R.string.pass),
                    onClick = {
                        viewModel.onPass()
                    },
                    enabled = isActionAllowed
                ),
            )

            GameButtons(
                buttonsInfo = buttonsInfo,
            )
        }

        if(isSkuchaDialogVisible) {
            GameResultAndSkuchaDialog(
                text = "Skucha!", textColor = Color(212, 212, 212),
                extraText = null
            )
        }

        val coinsBefore = coinsViewModel.coinsAmountAfterBetting.collectAsState()
        val betValue = coinsViewModel.betValue.collectAsState()

        if(isGameResultDialogVisible && isOpponentTurn) {
            SoundPlayer.playSound(SoundType.FAILURE)

            GameResultAndSkuchaDialog(
                text = "Defeat",
                textColor = DarkRed,
                extraText = if(coinsBefore.value == 0) "+ 50 starter coins to continue playing"
                else "- ${betValue.value} coins"
            )
        } else if(isGameResultDialogVisible) {
            SoundPlayer.playSound(SoundType.WIN)

            GameResultAndSkuchaDialog(
                text = "Win!",
                textColor = Color.Green,
                extraText = if(coinsBefore.value == 0 && betValue.value.toInt() == 0) "+ 50 starter coins to continue playing"
                    else "+ ${betValue.value} coins"
            )
        }

        if(viewModel.playerQuit) {
            SoundPlayer.playSound(SoundType.WIN)

            GameResultAndSkuchaDialog(
                text = "Win by giving up",
                textColor = Color.Green,
                extraText = if(coinsBefore.value == 0 && betValue.value.toInt() == 0) "+ 50 starter coins to continue playing"
                else "Opponent leave the game\n+${betValue.value} coins",
            )
        }

        if(viewModel.timerValue > -1) {
            GameResultAndSkuchaDialog(
                text = viewModel.timerValue.toString(),
                textColor = Color.White,
                extraText = "Waiting for opponent to return"
            )
        }
    }
}

@Composable
fun GameResultAndSkuchaDialog(text: String, extraText: String?, textColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.60f)
                .background(
                    color = Color(26, 26, 26, 200),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner))
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.small_padding))
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        drawStyle = Stroke(
                            width = 14f,
                            join = StrokeJoin.Round,
                            miter = 10f
                        )
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(-20f, 15f),
                            blurRadius = 20f
                        )
                    ),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }

            if (extraText != null) {
                Text(
                    text = extraText,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.medium_padding), horizontal = dimensionResource(R.dimen.small_padding))
                )
            }
        }
    }
}
