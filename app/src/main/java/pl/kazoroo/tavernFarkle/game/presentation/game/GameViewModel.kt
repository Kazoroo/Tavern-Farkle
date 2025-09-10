package pl.kazoroo.tavernFarkle.game.presentation.game
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.navigation.NavHostController
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import pl.kazoroo.tavernFarkle.core.data.presentation.BettingActions
//import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
//import pl.kazoroo.tavernFarkle.game.domain.model.Dice
//import pl.kazoroo.tavernFarkle.game.domain.model.DiceSetInfo
//import pl.kazoroo.tavernFarkle.game.domain.model.PointsState
//import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
//import pl.kazoroo.tavernFarkle.game.domain.usecase.CheckForSkuchaUseCase
//import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase
//import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
//import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundType
//import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
//import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
//
//class GameViewModel(
//    private val drawDiceUseCase: DrawDiceUseCase = DrawDiceUseCase(),
//    private val calculatePointsUseCase: CalculatePointsUseCase = CalculatePointsUseCase(),
//    private val checkForSkuchaUseCase: CheckForSkuchaUseCase = CheckForSkuchaUseCase(),
//    private val bettingActions: BettingActions,
//    private val ownedSpecialDices: List<OwnedSpecialDice>
//) : ViewModel() {
//    private val winningPoints: Int = 4000
//    private val _diceState = MutableStateFlow(
//        DiceSetInfo(
//            diceList = drawDiceUseCase(
//                ownedSpecialDices = ownedSpecialDices,
//                usedSpecialDice = emptyList(),
//                isDiceVisible = List(6) { true },
//                isOpponentTurn = false
//            ),
//            isDiceSelected = List(6) { false },
//            isDiceVisible = List(6) { true }
//        )
//    )
//    val diceState = _diceState.asStateFlow()
//
//    private val _userPointsState = MutableStateFlow(
//        PointsState(
//            selectedPoints = 0,
//            roundPoints = 0,
//            totalPoints = 0
//        )
//    )
//    val userPointsState = _userPointsState.asStateFlow()
//
//    private val _opponentPointsState = MutableStateFlow(
//        PointsState(
//            selectedPoints = 0,
//            roundPoints = 0,
//            totalPoints = 0
//        )
//    )
//    val opponentPointsState = _opponentPointsState.asStateFlow()
//
//    private val _skuchaState = MutableStateFlow(false)
//    val skuchaState = _skuchaState.asStateFlow()
//
//    private val _isOpponentTurn = MutableStateFlow(false)
//    val isOpponentTurn = _isOpponentTurn.asStateFlow()
//
//    private val _isGameEnd = MutableStateFlow(false)
//    val isGameEnd = _isGameEnd.asStateFlow()
//
//    private val _isDiceAnimating = MutableStateFlow(false)
//    val isDiceAnimating = _isDiceAnimating.asStateFlow()
//
//    private val _isDiceVisibleAfterGameEnd = MutableStateFlow(List(6) { false })
//    val isDiceVisibleAfterGameEnd = _isDiceVisibleAfterGameEnd.asStateFlow()
//
//    private var userUsedSpecialDice: List<Dice> = listOf()
//    private var opponentUsedSpecialDice: List<Dice> = listOf()
//
//    fun toggleDiceSelection(index: Int) {
//        _diceState.update { currentState ->
//            currentState.copy(
//                isDiceSelected = currentState.isDiceSelected.mapIndexed { i, selected ->
//                    if (i == index) !selected else selected
//                }
//            )
//        }
//
//        updateSelectedPointsState()
//    }
//
//    private fun updateSelectedPointsState() {
//        val stateToUpdate = if (_isOpponentTurn.value) _opponentPointsState else _userPointsState
//
//        stateToUpdate.update { currentState ->
//            currentState.copy(
//                selectedPoints = calculatePointsUseCase(
//                    diceList = diceState.value.diceList,
//                    isDiceSelected = diceState.value.isDiceSelected
//                )
//            )
//        }
//    }
//
//    /**
//     * Prepare the dice and points state for the next current player's throw.
//     */
//    fun prepareForNextThrow() {
//        val isDiceVisible = getUpdatedDiceVisibility()
//        val usedSpecialDice = if(_isOpponentTurn.value) opponentUsedSpecialDice else userUsedSpecialDice
//
//        viewModelScope.launch {
//            val allDiceInvisible = isDiceVisible.all { !it }
//            if(allDiceInvisible) {
//                triggerDiceRowAnimation(
//                    isDiceVisible = List(6) { true },
//                    usedSpecialDice = emptyList()
//                )
//            } else {
//                triggerDiceRowAnimation(
//                    isDiceVisible = isDiceVisible,
//                    usedSpecialDice = usedSpecialDice.map { it.specialDiceName!! }
//                )
//            }
//        }
//        _diceState.update { currentState ->
//            currentState.copy(
//                isDiceSelected = List(6) { false },
//                isDiceVisible = isDiceVisible
//            )
//        }
//
//        val stateToUpdate = if (_isOpponentTurn.value) _opponentPointsState else _userPointsState
//
//        stateToUpdate.update { currentState ->
//            currentState.copy(
//                roundPoints = stateToUpdate.value.selectedPoints + stateToUpdate.value.roundPoints,
//                selectedPoints = 0
//            )
//        }
//    }
//
//    private fun getUpdatedDiceVisibility(): MutableList<Boolean> {
//        val state = diceState.value
//        val newIsDiceVisible: MutableList<Boolean> = state.isDiceVisible.toMutableList()
//        val filteredDice = state.diceList.filterIndexed { index, dice ->
//            dice.specialDiceName != null && state.isDiceSelected[index]
//        }
//
//        if (_isOpponentTurn.value) {
//            synchronized(opponentUsedSpecialDice) {
//                opponentUsedSpecialDice += filteredDice
//            }
//        } else {
//            synchronized(userUsedSpecialDice) {
//                userUsedSpecialDice += filteredDice
//            }
//        }
//
//
//        for (i in state.isDiceVisible.indices) {
//            if (state.isDiceVisible[i] && state.isDiceSelected[i]) {
//                newIsDiceVisible[i] = false
//            }
//        }
//
//        return newIsDiceVisible
//    }
//
//    fun checkForSkucha(navController: NavHostController) {
//        if(startNewRoundIfAllDiceInvisible(navController)) return
//
//        val isSkucha = checkForSkuchaUseCase(
//            diceState.value.diceList,
//            diceState.value.isDiceVisible
//        )
//
//        if(isSkucha) {
//            viewModelScope.launch {
//                performSkuchaActions(navController)
//            }
//        }
//    }
//
//    private fun startNewRoundIfAllDiceInvisible(navController: NavHostController): Boolean {
//        return if (diceState.value.isDiceVisible.all { !it }) {
//            if (_isOpponentTurn.value) {
//                opponentUsedSpecialDice = emptyList()
//            } else {
//                userUsedSpecialDice = emptyList()
//            }
//
//            _diceState.update { currentState ->
//                currentState.copy(
//                    isDiceSelected = List(6) { false },
//                    isDiceVisible = List(6) { true }
//                )
//            }
//            checkForSkucha(navController)
//            true
//        } else {
//            false
//        }
//    }
//
//    private suspend fun performSkuchaActions(navController: NavHostController) {
//        val stateToUpdate = if (_isOpponentTurn.value) _opponentPointsState else _userPointsState
//        if (_isOpponentTurn.value) {
//            opponentUsedSpecialDice = emptyList()
//        } else {
//            userUsedSpecialDice = emptyList()
//        }
//
//        delay(1000L)
//        _skuchaState.value = true
//        SoundPlayer.playSound(SoundType.SKUCHA)
//
//        delay(3000L)
//        _skuchaState.value = false
//
//        stateToUpdate.update { currentState ->
//            currentState.copy(
//                roundPoints = 0
//            )
//        }
//
//        triggerDiceRowAnimation(
//            isDiceVisible = List(6) { true },
//            isOpponentTurn = !_isOpponentTurn.value
//        )
//
//        _diceState.update { currentState ->
//            currentState.copy(
//                isDiceSelected = List(6) { false },
//                isDiceVisible = List(6) { true }
//            )
//        }
//
//        if(_isOpponentTurn.value) {
//            _isOpponentTurn.value = false
//        } else {
//            computerPlayerTurn(navController)
//        }
//    }
//
//    fun passTheRound(navController: NavHostController) {
//        viewModelScope.launch {
//            if (_isOpponentTurn.value) {
//                opponentUsedSpecialDice = emptyList()
//            } else {
//                userUsedSpecialDice = emptyList()
//            }
//            val stateToUpdate = if(_isOpponentTurn.value) _opponentPointsState else _userPointsState
//
//            stateToUpdate.update { currentState ->
//                currentState.copy(
//                    roundPoints = 0,
//                    selectedPoints = 0,
//                    totalPoints = stateToUpdate.value.selectedPoints + stateToUpdate.value.roundPoints + stateToUpdate.value.totalPoints
//                )
//            }
//
//            if (stateToUpdate.value.totalPoints >= winningPoints) {
//                performGameEndActions(navController)
//
//                return@launch
//            }
//            _diceState.update { currentState ->
//                currentState.copy(
//                    isDiceVisible = getUpdatedDiceVisibility()
//                )
//            }
//
//            triggerDiceRowAnimation(
//                isDiceVisible = List(6) { true },
//                usedSpecialDice = emptyList(),
//                isTurnPassed = true
//            )
//
//            _diceState.update { currentState ->
//                currentState.copy(
//                    isDiceSelected = List(6) { false },
//                    isDiceVisible = List(6) { true }
//                )
//            }
//
//            if (!isOpponentTurn.value) {
//                computerPlayerTurn(navController)
//            } else {
//                _isOpponentTurn.value = false
//            }
//        }
//    }
//
//    private suspend fun triggerDiceRowAnimation(
//        isDiceVisible: List<Boolean> = diceState.value.isDiceVisible,
//        usedSpecialDice: List<SpecialDiceName> = this.userUsedSpecialDice.map { it.specialDiceName!! },
//        isTurnPassed: Boolean = false,
//        isOpponentTurn: Boolean = _isOpponentTurn.value
//    ) {
//        delay(300L) //Waiting for selected dice horizontal slide animation finish
//        _isDiceAnimating.value = true
//        delay(500L)
//        SoundPlayer.playSound(SoundType.DICE_ROLLING)
//        _diceState.update { currentState ->
//            currentState.copy(
//                diceList = drawDiceUseCase(
//                    ownedSpecialDices = ownedSpecialDices,
//                    usedSpecialDice = usedSpecialDice,
//                    isDiceVisible = isDiceVisible,
//                    isOpponentTurn = if(isTurnPassed) !isOpponentTurn else isOpponentTurn
//                )
//            )
//        }
//        delay(500L)
//        _isDiceAnimating.value = false
//    }
//
//    private suspend fun performGameEndActions(
//        navController: NavHostController
//    ) {
//        _isDiceVisibleAfterGameEnd.value = getUpdatedDiceVisibility().map { !it }
//
//        _diceState.update { currentState ->
//            currentState.copy(
//                isDiceSelected = List(6) { false }
//            )
//        }
//
//        _isGameEnd.value = true
//
//        val isOpponentWin = _opponentPointsState.value.totalPoints >= winningPoints
//        if(isOpponentWin) {
//            SoundPlayer.playSound(SoundType.FAILURE)
//        } else {
//            SoundPlayer.playSound(SoundType.WIN)
//            bettingActions.addBetCoinsToTotalCoinsAmount()
//        }
//
//        delay(3000L)
//
//        _isGameEnd.value = false
//
//        navController.navigate(Screen.MainScreen.withArgs()) {
//            popUpTo(Screen.GameScreen.withArgs()) { inclusive = true }
//        }
//
//        delay(1000L)
//
//        resetState()
//    }
//
//    /**
//     * Reset the value of diceState, opponentPointsState, userPointsState, isOpponentTurn and isDiceVisibleAfterGameEnd to their default values.
//     */
//    private fun resetState() {
//        val usedSpecialDice = if (_isOpponentTurn.value) opponentUsedSpecialDice else userUsedSpecialDice
//
//        _diceState.update { currentState ->
//            currentState.copy(
//                diceList = drawDiceUseCase(
//                    ownedSpecialDices = ownedSpecialDices,
//                    usedSpecialDice = usedSpecialDice.map { it.specialDiceName!! },
//                    isDiceVisible = diceState.value.isDiceVisible,
//                    isOpponentTurn = isOpponentTurn.value
//                ),
//                isDiceSelected = List(6) { false },
//                isDiceVisible = List(6) { true }
//            )
//        }
//        _opponentPointsState.update { currentState ->
//            currentState.copy(
//                selectedPoints = 0,
//                roundPoints = 0,
//                totalPoints = 0
//            )
//        }
//        _userPointsState.update { currentState ->
//            currentState.copy(
//                selectedPoints = 0,
//                roundPoints = 0,
//                totalPoints = 0
//            )
//        }
//        _isOpponentTurn.value = false
//        _isDiceVisibleAfterGameEnd.value = List(6) { false }
//    }
//
//    private fun computerPlayerTurn(navController: NavHostController) {
//        _isOpponentTurn.value = true
//
//        viewModelScope.launch(Dispatchers.Default) {
//            val playingUntilDiceLeft = (2..3).random()
//
//            while(diceState.value.isDiceVisible.count { it } > playingUntilDiceLeft) {
//                delay((1600L..2000L).random())
//
//                val indexesOfDiceGivingPoints = searchForDiceIndexGivingPoints()
//
//                if(indexesOfDiceGivingPoints.isEmpty()) {
//                    performSkuchaActions(navController)
//
//                    return@launch
//                }
//
//                for (i in indexesOfDiceGivingPoints.indices) {
//                    toggleDiceSelection(indexesOfDiceGivingPoints[i])
//                    delay((1200L..1600L).random())
//                }
//
//                val shouldOpponentKeepPlaying =
//                    diceState.value.isDiceVisible.count { it } - diceState.value.isDiceSelected.count { it } > playingUntilDiceLeft
//                if(shouldOpponentKeepPlaying) {
//                    prepareForNextThrow()
//                } else {
//                    withContext(Dispatchers.Main) {
//                        passTheRound(navController)
//                    }
//                    break
//                }
//            }
//
//            opponentUsedSpecialDice = emptyList()
//        }
//    }
//
//    /**
//     * @return List of dice indexes that gives points
//     */
//    private fun searchForDiceIndexGivingPoints(): List<Int> {
//        val sequenceDice: List<Int> =
//            diceState.value.diceList.filterIndexed { index, _ -> diceState.value.isDiceVisible[index] }
//                .groupingBy { it.value }.eachCount().filter { it.value >= 3 }.keys.toList()
//
//        val indexesOfDiceGivingPoints = diceState.value.diceList.mapIndexedNotNull { index, dice ->
//            if ((dice.value == 1 || dice.value == 5 || sequenceDice.contains(dice.value)) && diceState.value.isDiceVisible[index]) index else null
//        }.shuffled()
//
//        return indexesOfDiceGivingPoints
//    }
//}