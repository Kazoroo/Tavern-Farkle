package pl.kazoroo.tavernFarkle.game.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.presentation.BettingActions
import pl.kazoroo.tavernFarkle.game.domain.model.TableData
import pl.kazoroo.tavernFarkle.game.presentation.components.ButtonInfo
import pl.kazoroo.tavernFarkle.game.presentation.components.SpeedDialMenu
import pl.kazoroo.tavernFarkle.game.presentation.game.components.ExitDialog
import pl.kazoroo.tavernFarkle.game.presentation.game.components.GameButtons
import pl.kazoroo.tavernFarkle.game.presentation.game.components.InteractiveDiceLayout
import pl.kazoroo.tavernFarkle.game.presentation.game.components.PointsTable
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel
import pl.kazoroo.tavernFarkle.ui.theme.DarkRed

@Composable
fun GameScreen(
    bettingActions: BettingActions,
    navController: NavHostController,
    inventoryViewModel: InventoryViewModel
) {
    val viewModel =  remember {
        GameViewModel(
            bettingActions = bettingActions,
            ownedSpecialDices = inventoryViewModel.ownedSpecialDice.value
        )
    }

    val isSkucha = viewModel.skuchaState.collectAsState().value
    val isGameEnd = viewModel.isGameEnd.collectAsState().value
    val isOpponentTurn = viewModel.isOpponentTurn.collectAsState().value
    val selectedPoints = viewModel.userPointsState.collectAsState().value.selectedPoints
    val tableData = listOf(
        TableData(
            pointsType = stringResource(R.string.total),
            yourPoints = viewModel.userPointsState.collectAsState().value.totalPoints.toString(),
            opponentPoints = viewModel.opponentPointsState.collectAsState().value.totalPoints.toString()
        ),
        TableData(
            pointsType = stringResource(R.string.round),
            yourPoints = viewModel.userPointsState.collectAsState().value.roundPoints.toString(),
            opponentPoints = viewModel.opponentPointsState.collectAsState().value.roundPoints.toString()
        ),
        TableData(
            pointsType = stringResource(R.string.selected_forDices),
            yourPoints = selectedPoints.toString(),
            opponentPoints = viewModel.opponentPointsState.collectAsState().value.selectedPoints.toString()
        ),
    )
    val scope = rememberCoroutineScope()
    val showExitDialog = remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog.value = true
    }

    LaunchedEffect(isOpponentTurn) {
        if(!isOpponentTurn) {
            viewModel.checkForSkucha(navController)
        }
    }

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
        Image(
            painter = painterResource(id = R.drawable.wooden_background_texture),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box {
                PointsTable(
                    data = tableData,
                    isOpponentTurn = isOpponentTurn
                )
                SpeedDialMenu(
                    modifier = Modifier.align(Alignment.TopStart),
                )
            }
            InteractiveDiceLayout(
                diceState = viewModel.diceState.collectAsState().value,
                diceOnClick = { index ->
                    if(!viewModel.skuchaState.value) {
                        viewModel.toggleDiceSelection(index)
                    }
                },
                isDiceClickable = !isOpponentTurn && !isGameEnd,
                isDiceAnimating = viewModel.isDiceAnimating.collectAsState().value,
                isDiceVisibleAfterGameEnd = viewModel.isDiceVisibleAfterGameEnd.collectAsState().value
            )
            Spacer(modifier = Modifier.weight(1f))

            val buttonsInfo = listOf(
                ButtonInfo(
                    text = stringResource(id = R.string.score_and_roll_again),
                    onClick = {
                        if(!isSkucha) {
                            scope.launch {
                                viewModel.prepareForNextThrow()
                                delay(1000L)
                                viewModel.checkForSkucha(navController)
                            }
                        } else { Unit }
                    },
                    enabled = (selectedPoints != 0 && !isOpponentTurn) && !isGameEnd
                ),
                ButtonInfo(
                    text = stringResource(id = R.string.pass),
                    onClick = {
                        if(!isSkucha) {
                            viewModel.passTheRound(navController)
                        } else { Unit }
                    },
                    enabled = (selectedPoints != 0 && !isOpponentTurn) && !isGameEnd
                ),
            )

            GameButtons(
                buttonsInfo = buttonsInfo,
            )
        }

        @Composable
        fun gameResultAndSkuchaDialog(text: String, textColor: Color) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = textColor,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier
                        .background(
                            color = Color(26, 26, 26, 220),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner))
                        )
                        .padding(dimensionResource(id = R.dimen.medium_padding))
                )
            }
        }

        var isSkuchaDialogVisible by remember { mutableStateOf(false) }
        var isGameResultDialogVisible by remember { mutableStateOf(false) }

        LaunchedEffect(isSkucha) {
            isSkuchaDialogVisible = isSkucha
        }

        LaunchedEffect(isGameEnd) {
            delay(1000L)

            isGameResultDialogVisible = isGameEnd
        }

        if(isSkuchaDialogVisible) {
            gameResultAndSkuchaDialog(text = "Skucha!", textColor = Color(212, 212, 212))
        }

        if(isGameResultDialogVisible && isOpponentTurn) {
            gameResultAndSkuchaDialog(text = "Defeat", textColor = DarkRed)
        } else if(isGameResultDialogVisible) {
            gameResultAndSkuchaDialog(text = "Win!", textColor = Color.Green)
        }
    }
}


