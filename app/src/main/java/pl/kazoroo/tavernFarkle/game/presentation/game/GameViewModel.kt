package pl.kazoroo.tavernFarkle.game.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.kazoroo.tavernFarkle.core.data.presentation.BettingActions
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.game.domain.model.DiceSetInfo
import pl.kazoroo.tavernFarkle.game.domain.model.PointsState
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.CheckForSkuchaUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundType

class GameViewModel(
    private val drawDiceUseCase: DrawDiceUseCase = DrawDiceUseCase(),
    private val calculatePointsUseCase: CalculatePointsUseCase = CalculatePointsUseCase(),
    private val checkForSkuchaUseCase: CheckForSkuchaUseCase = CheckForSkuchaUseCase(),
    private val bettingActions: BettingActions
) : ViewModel() {
    private val winningPoints: Int = 4000
    private val _diceState = MutableStateFlow(
        DiceSetInfo(
            diceList = drawDiceUseCase(),
            isDiceSelected = List(6) { false },
            isDiceVisible = List(6) { true }
        )
    )
    val diceState = _diceState.asStateFlow()

    private val _userPointsState = MutableStateFlow(
        PointsState(
            selectedPoints = 0,
            roundPoints = 0,
            totalPoints = 0
        )
    )
    val userPointsState = _userPointsState.asStateFlow()

    private val _opponentPointsState = MutableStateFlow(
        PointsState(
            selectedPoints = 0,
            roundPoints = 0,
            totalPoints = 0
        )
    )
    val opponentPointsState = _opponentPointsState.asStateFlow()

    private val _skuchaState = MutableStateFlow(false)
    val skuchaState = _skuchaState.asStateFlow()

    private val _isOpponentTurn = MutableStateFlow(false)
    val isOpponentTurn = _isOpponentTurn.asStateFlow()

    private val _isGameEnd = MutableStateFlow(false)
    val isGameEnd = _isGameEnd.asStateFlow()

    private val _isDiceAnimating = MutableStateFlow(false)
    val isDiceAnimating = _isDiceAnimating.asStateFlow()

    private val _isDiceVisibleAfterGameEnd = MutableStateFlow(List(6) { false })
    val isDiceVisibleAfterGameEnd = _isDiceVisibleAfterGameEnd.asStateFlow()

    fun toggleDiceSelection(index: Int) {
        _diceState.update { currentState ->
            val updatedDiceSelected = currentState.isDiceSelected.toMutableList()
            updatedDiceSelected[index] = !updatedDiceSelected[index]

            currentState.copy(isDiceSelected = updatedDiceSelected)
        }

        updateSelectedPointsState()
    }

    private fun updateSelectedPointsState() {
        val stateToUpdate = if (_isOpponentTurn.value) _opponentPointsState else _userPointsState

        stateToUpdate.update { currentState ->
            currentState.copy(
                selectedPoints = calculatePointsUseCase(
                    diceList = diceState.value.diceList,
                    isDiceSelected = diceState.value.isDiceSelected
                )
            )
        }
    }

    /**
     * Prepare the dice and points state for the next current player's throw.
     */
    fun prepareForNextThrow() {
        viewModelScope.launch {
            triggerDiceRowAnimation()
        }

        _diceState.update { currentState ->
            currentState.copy(
                isDiceSelected = List(6) { false },
                isDiceVisible = getUpdatedDiceVisibility()
            )
        }

        val stateToUpdate = if (_isOpponentTurn.value) _opponentPointsState else _userPointsState

        stateToUpdate.update { currentState ->
            currentState.copy(
                roundPoints = stateToUpdate.value.selectedPoints + stateToUpdate.value.roundPoints,
                selectedPoints = 0
            )
        }
    }

    private fun getUpdatedDiceVisibility(): MutableList<Boolean> {
        val newIsDiceVisible: MutableList<Boolean> = diceState.value.isDiceVisible.toMutableList()

        for (i in diceState.value.isDiceVisible.indices) {
            if (diceState.value.isDiceVisible[i] && diceState.value.isDiceSelected[i]) {
                newIsDiceVisible[i] = false
            }
        }

        return newIsDiceVisible
    }

    fun checkForSkucha(navController: NavHostController) {
        if(startNewRoundIfAllDiceInvisible(navController)) return

        val isSkucha = checkForSkuchaUseCase(
            diceState.value.diceList,
            diceState.value.isDiceVisible
        )

        if(isSkucha) {
            viewModelScope.launch {
                performSkuchaActions(navController)
            }
        }
    }

    private fun startNewRoundIfAllDiceInvisible(navController: NavHostController): Boolean {
        return if (diceState.value.isDiceVisible.all { !it }) {
            _diceState.update { currentState ->
                currentState.copy(
                    isDiceSelected = List(6) { false },
                    isDiceVisible = List(6) { true }
                )
            }
            checkForSkucha(navController)
            true
        } else {
            false
        }
    }

    private suspend fun performSkuchaActions(navController: NavHostController) {
        val stateToUpdate = if (_isOpponentTurn.value) _opponentPointsState else _userPointsState

        delay(1000L)
        _skuchaState.value = true
        SoundPlayer.playSound(SoundType.SKUCHA)

        delay(3000L)
        _skuchaState.value = false

        stateToUpdate.update { currentState ->
            currentState.copy(
                roundPoints = 0
            )
        }

        triggerDiceRowAnimation()

        _diceState.update { currentState ->
            currentState.copy(
                isDiceSelected = List(6) { false },
                isDiceVisible = List(6) { true }
            )
        }

        if(_isOpponentTurn.value) {
            _isOpponentTurn.value = false
        } else {
            computerPlayerTurn(navController)
        }
    }

    fun passTheRound(navController: NavHostController) {
        viewModelScope.launch {
            val stateToUpdate = if(_isOpponentTurn.value) _opponentPointsState else _userPointsState

            stateToUpdate.update { currentState ->
                currentState.copy(
                    roundPoints = 0,
                    selectedPoints = 0,
                    totalPoints = stateToUpdate.value.selectedPoints + stateToUpdate.value.roundPoints + stateToUpdate.value.totalPoints
                )
            }

            if (stateToUpdate.value.totalPoints >= winningPoints) {
                performGameEndActions(navController)

                return@launch
            }
            _diceState.update { currentState ->
                currentState.copy(
                    isDiceVisible = getUpdatedDiceVisibility()
                )
            }

            triggerDiceRowAnimation()

            _diceState.update { currentState ->
                currentState.copy(
                    isDiceSelected = List(6) { false },
                    isDiceVisible = List(6) { true }
                )
            }

            if (!isOpponentTurn.value) {
                computerPlayerTurn(navController)
            } else {
                _isOpponentTurn.value = false
            }
        }
    }

    private suspend fun triggerDiceRowAnimation() {
        delay(300L) //Waiting for selected dice horizontal slide animation finish
        _isDiceAnimating.value = true
        delay(500L)
        SoundPlayer.playSound(SoundType.DICE_ROLLING)
        _diceState.update { currentState ->
            currentState.copy(
                diceList = drawDiceUseCase()
            )
        }
        delay(500L)
        _isDiceAnimating.value = false
    }

    private suspend fun performGameEndActions(
        navController: NavHostController
    ) {
        _isDiceVisibleAfterGameEnd.value = getUpdatedDiceVisibility().map { !it }

        _diceState.update { currentState ->
            currentState.copy(
                isDiceSelected = List(6) { false }
            )
        }

        _isGameEnd.value = true

        if(_opponentPointsState.value.totalPoints >= winningPoints) {
            SoundPlayer.playSound(SoundType.FAILURE)
        } else {
            SoundPlayer.playSound(SoundType.WIN)
            bettingActions.addBetCoinsToTotalCoinsAmount()
        }

        delay(3000L)

        _isGameEnd.value = false

        navController.navigate(Screen.MainScreen.withArgs()) {
            popUpTo(Screen.GameScreen.withArgs()) { inclusive = true }
        }

        delay(1000L)

        resetState()
    }

    /**
     * Reset the value of diceState, opponentPointsState, userPointsState, isOpponentTurn and isDiceVisibleAfterGameEnd to their default values.
     */
    private fun resetState() {
        _diceState.update { currentState ->
            currentState.copy(
                diceList = drawDiceUseCase(),
                isDiceSelected = List(6) { false },
                isDiceVisible = List(6) { true }
            )
        }
        _opponentPointsState.update { currentState ->
            currentState.copy(
                selectedPoints = 0,
                roundPoints = 0,
                totalPoints = 0
            )
        }
        _userPointsState.update { currentState ->
            currentState.copy(
                selectedPoints = 0,
                roundPoints = 0,
                totalPoints = 0
            )
        }
        _isOpponentTurn.value = false
        _isDiceVisibleAfterGameEnd.value = List(6) { false }
    }

    private fun computerPlayerTurn(navController: NavHostController) {
        _isOpponentTurn.value = true

        viewModelScope.launch(Dispatchers.Default) {
            val minDiceCount = (2..4).random()
            while(diceState.value.isDiceVisible.count { it } > minDiceCount) {
                delay((1600L..2000L).random())

                val indexesOfDiceGivingPoints = searchForDiceIndexGivingPoints()

                if(indexesOfDiceGivingPoints.isEmpty()) {
                    performSkuchaActions(navController)

                    return@launch
                }

                for (i in indexesOfDiceGivingPoints.indices) {
                    toggleDiceSelection(indexesOfDiceGivingPoints[i])
                    delay((1200L..1600L).random())
                }

                if(diceState.value.isDiceVisible.count { it } - diceState.value.isDiceSelected.count { it } > minDiceCount) {
                    prepareForNextThrow()
                } else {
                    withContext(Dispatchers.Main) {
                        passTheRound(navController)
                    }
                    break
                }
            }
        }
    }

    /**
     * @return List of dice indexes that gives points
     */
    private fun searchForDiceIndexGivingPoints(): List<Int> {
        val sequenceDice: List<Int> =
            diceState.value.diceList.filterIndexed { index, _ -> diceState.value.isDiceVisible[index] }
                .groupingBy { it.value }.eachCount().filter { it.value >= 3 }.keys.toList()

        val indexesOfDiceGivingPoints = diceState.value.diceList.mapIndexedNotNull { index, dice ->
            if ((dice.value == 1 || dice.value == 5 || sequenceDice.contains(dice.value)) && diceState.value.isDiceVisible[index]) index else null
        }.shuffled()

        return indexesOfDiceGivingPoints
    }
}