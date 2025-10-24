package pl.kazoroo.tavernFarkle.singleplayer.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CheckGameEndUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.PlayerStatus
import pl.kazoroo.tavernFarkle.singleplayer.domain.usecase.PlayOpponentTurnUseCase

class GameViewModel(
    private val repository: GameRepository,
    private val calculatePointsUseCase: CalculatePointsUseCase,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val playOpponentTurnUseCase: PlayOpponentTurnUseCase,
    private val checkGameEndUseCase: CheckGameEndUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val isMultiplayer: Boolean
): ViewModel() {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    val gameState: StateFlow<GameState> = repository.gameState

    val isOpponentTurn: StateFlow<Boolean> =
        gameState.map { state ->
            state.currentPlayerUuid != repository.myUuidState.value
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L, 0), false)

    private val _isDiceAnimating = MutableStateFlow(true)
    val isDiceAnimating: StateFlow<Boolean> = _isDiceAnimating

    private val _showGameEndDialog = MutableStateFlow(false)
    val showGameEndDialog: StateFlow<Boolean> = _showGameEndDialog

    private val _showSkuchaDialog = MutableStateFlow(false)
    val showSkuchaDialog: StateFlow<Boolean> = _showSkuchaDialog

    val myPlayerIndex = repository.getMyPlayerIndex()
    val opponentPlayerIndex: StateFlow<Int?> =
        repository.getOpponentPlayerIndex()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), null)

    var playerQuit by mutableStateOf(false)
        private set

    init {
        observeSkucha()
        observeDiceAnimation()
    }

    fun onGameEnd(navController: NavHostController) {
        viewModelScope.launch {
            repository.gameState.collect { game ->
                if (game.isGameEnd) {
                    viewModelScope.launch {
                        delay(1000L)
                        _showGameEndDialog.value = true
                        delay(3000L)
                        _showGameEndDialog.value = false

                        navController.navigate(Screen.MainScreen.withArgs()) {
                            popUpTo(Screen.GameScreen.withArgs()) { inclusive = true }
                        }

                        repository.removeLobbyNode()
                    }
                }
            }
        }
    }

    fun toggleDiceSelection(index: Int) {
        repository.toggleDiceSelection(index)
        val currentPlayerDiceSet = gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet
        calculatePointsUseCase(
            diceList = currentPlayerDiceSet,
            repository = repository
        )
    }

    fun onPass(addBetCoinsToTotalCoins: () -> Unit) {
        repository.sumTotalPoints()

        if(checkGameEndUseCase(repository = repository, sumCoins = addBetCoinsToTotalCoins)) return

        scope.launch {
            repository.toggleDiceRowAnimation()
            delay(600L)
            repository.resetDiceState()
            repository.changeCurrentPlayer()
            drawDiceUseCase(
                repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet,
                repository = repository
            )

            val isOpponentTurn = repository.gameState.value.currentPlayerUuid != repository.myUuidState.value

            if(isOpponentTurn && !isMultiplayer) {
                playOpponentTurnUseCase()
            }
        }
    }

    fun onScoreAndRollAgain() {
        repository.sumRoundPoints()

        scope.launch {
            repository.hideSelectedDice()
            repository.toggleDiceRowAnimation()
            delay(600L)

            if(repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet.all { !it.isVisible }) {
                repository.resetDiceState()
            }

            drawDiceUseCase(
                repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet,
                repository = repository
            )
        }
    }

    private fun observeSkucha() {
        scope.launch {
            repository.gameState
                .map { it.isSkucha }
                .distinctUntilChanged()
                .collect { isSkucha ->
                    if(isSkucha) {
                        launch {
                            delay(1000L)
                            SoundPlayer.playSound(SoundType.SKUCHA)
                            _showSkuchaDialog.value = true
                            delay(2000L)
                            _showSkuchaDialog.value = false

                            val isHost = gameState.value.players[0].uuid == repository.myUuidState.value
                            if(isHost) {
                                repository.toggleSkucha()
                                repository.resetRoundAndSelectedPoints()
                            }

                            repository.toggleDiceRowAnimation()
                            delay(600L)

                            if(isHost) {
                                repository.resetDiceState()
                                repository.changeCurrentPlayer()

                                drawDiceUseCase(
                                    repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet,
                                    repository = repository
                                )
                            }

                            val isOpponentTurn = repository.gameState.value.currentPlayerUuid != repository.myUuidState.value

                            if(isOpponentTurn && !isMultiplayer) {
                                playOpponentTurnUseCase()
                            }
                        }
                    }
                }
        }
    }

    private fun observeDiceAnimation() {
        scope.launch {
            repository.gameState
                .map { it.isAnimating }
                .distinctUntilChanged()
                .collect {
                    delay(200L)
                    _isDiceAnimating.value = true
                    delay(500L)
                    SoundPlayer.playSound(SoundType.DICE_ROLLING)
                    delay(500L)
                    _isDiceAnimating.value = false
                }
        }
    }

    fun onQuit() {
        repository.updatePlayerStatus(PlayerStatus.LEFT)
    }

    fun observePlayerStatus(navController: NavHostController, addCoinsReward: () -> Unit) {
        scope.launch {
            opponentPlayerIndex
                .filterNotNull()
                .distinctUntilChanged()
                .collect { opponentPlayerIndex ->
                    repository.gameState
                        .map { it.players[opponentPlayerIndex].status }
                        .distinctUntilChanged()
                        .collect {
                            when (it) {
                                PlayerStatus.LEFT -> {
                                    if(repository.gameState.value.players[opponentPlayerIndex].status == PlayerStatus.LEFT) {
                                        playerQuit = true
                                        delay(2500L)
                                        repository.removeLobbyNode()

                                        addCoinsReward()
                                        navController.navigate(Screen.MainScreen.withArgs()) {
                                            popUpTo(Screen.GameScreen.withArgs()) { inclusive = true }
                                        }
                                    }
                                }

                                else -> {}
                            }
                        }
                }
        }
    }
}

