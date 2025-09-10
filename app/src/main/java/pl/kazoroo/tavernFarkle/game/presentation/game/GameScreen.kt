package pl.kazoroo.tavernFarkle.game.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.game.domain.model.TableData
import pl.kazoroo.tavernFarkle.game.presentation.components.ButtonInfo
import pl.kazoroo.tavernFarkle.game.presentation.components.SpeedDialMenu
import pl.kazoroo.tavernFarkle.game.presentation.game.components.ExitDialog
import pl.kazoroo.tavernFarkle.game.presentation.game.components.GameButtons
import pl.kazoroo.tavernFarkle.game.presentation.game.components.InteractiveDiceLayout
import pl.kazoroo.tavernFarkle.game.presentation.game.components.PointsTable
import pl.kazoroo.tavernFarkle.ui.theme.DarkRed
import java.util.UUID

@Composable
fun GameScreen(
    navController: NavHostController,
    playerUuid: UUID,
    viewModel: GameViewModelRefactor
) {
    val state by viewModel.gameState.collectAsState()

    val currentPlayerIndex = state.players.indexOfFirst {
        it.uuid == state.currentPlayerUuid
    }
    val isOpponentTurn = state.currentPlayerUuid == playerUuid
    val selectedPoints = state.players[0].selectedPoints

    val tableData = listOf(
        TableData(
            pointsType = stringResource(R.string.total),
            yourPoints = state.players[0].totalPoints.toString(),
            opponentPoints = state.players[1].totalPoints.toString()
        ),
        TableData(
            pointsType = stringResource(R.string.round),
            yourPoints = state.players[0].roundPoints.toString(),
            opponentPoints = state.players[1].roundPoints.toString()
        ),
        TableData(
            pointsType = stringResource(R.string.selected_forDices),
            yourPoints = selectedPoints.toString(),
            opponentPoints = state.players[1].selectedPoints.toString()
        ),
    )
    val showExitDialog = remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog.value = true
    }

    // TODO: here was LaunchedEffect checking for skucha, may be needed in future.

    if(showExitDialog.value) {
        ExitDialog(
            onDismissClick = { showExitDialog.value = false },
            onQuitClick = {
                showExitDialog.value = false
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

                SpeedDialMenu(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .systemBarsPadding()
                        .padding(start = dimensionResource(R.dimen.small_padding)),
                    navController = navController,
                    restricted = true
                )
            }

            InteractiveDiceLayout(
                diceState = state.players[currentPlayerIndex].diceSet,
                diceOnClick = { index ->
                    if(!state.isSkucha) {
                        viewModel.toggleDiceSelection(index)
                    }
                },
                isDiceClickable = !isOpponentTurn && !state.isGameEnd,
                isDiceAnimating = false, //viewModel.isDiceAnimating.collectAsState().value, TODO: add animations
            )
            Spacer(modifier = Modifier.weight(1f))

            val buttonsInfo = listOf(
                ButtonInfo(
                    text = stringResource(id = R.string.score_and_roll_again),
                    onClick = {
                        viewModel.onScoreAndRollAgain()
                    },
                    enabled = (selectedPoints != 0 && !isOpponentTurn) && !state.isGameEnd
                ),
                ButtonInfo(
                    text = stringResource(id = R.string.pass),
                    onClick = {
                        viewModel.onPass()
                    },
                    enabled = (selectedPoints != 0 && !isOpponentTurn) && !state.isGameEnd
                ),
            )

            GameButtons(
                buttonsInfo = buttonsInfo,
            )
        }

        @Composable
        fun gameResultAndSkuchaDialog(text: String, extraText: String?, textColor: Color) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.wrapContentSize()
                        .background(
                            color = Color(26, 26, 26, 220),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner))
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = text,
                        color = textColor,
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.medium_padding))
                    )

                    if(extraText != null) {
                        Text(
                            text = extraText,
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.medium_padding))
                        )
                    }
                }
            }
        }

        var isSkuchaDialogVisible by remember { mutableStateOf(false) }
        var isGameResultDialogVisible by remember { mutableStateOf(false) }

        LaunchedEffect(state.isSkucha) {
            isSkuchaDialogVisible = state.isSkucha
        }

        LaunchedEffect(state.isGameEnd) {
            delay(1000L)

            isGameResultDialogVisible = state.isGameEnd
        }

        if(isSkuchaDialogVisible) {
            gameResultAndSkuchaDialog(
                text = "Skucha!", textColor = Color(212, 212, 212),
                extraText = null
            )
        }

        if(isGameResultDialogVisible && isOpponentTurn) {
            gameResultAndSkuchaDialog(
                text = "Defeat", textColor = DarkRed,
                extraText = "Next time will be better!"
            )
        } else if(isGameResultDialogVisible) {
            gameResultAndSkuchaDialog(
                text = "Win!", textColor = Color.Green,
                extraText = "You are the champion!"
            )
        }
    }
}
