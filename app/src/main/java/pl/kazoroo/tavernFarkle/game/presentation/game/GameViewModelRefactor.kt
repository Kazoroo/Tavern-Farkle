package pl.kazoroo.tavernFarkle.game.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.PlayOpponentTurnUseCase
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundType

class GameViewModelRefactor(
    private val repository: GameRepository,
    private val calculatePointsUseCase: CalculatePointsUseCase,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val playOpponentTurnUseCase: PlayOpponentTurnUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    val gameState: StateFlow<GameState> = repository.gameState

    val isOpponentTurn: StateFlow<Boolean> =
        gameState.map { state ->
            state.currentPlayerUuid != repository.myUuidState.value
        }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val _isDiceAnimating = MutableStateFlow(false)
    val isDiceAnimating: StateFlow<Boolean> = _isDiceAnimating

    init {
        observeSkucha()
    }

    fun toggleDiceSelection(index: Int) {
        repository.toggleDiceSelection(index)
        val currentPlayerDiceSet = gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet
        calculatePointsUseCase(currentPlayerDiceSet)
    }

    fun onPass() {
        scope.launch {
            repository.sumTotalPoints()
            triggerDiceRowAnimation()
            repository.resetDiceState()
            repository.changeCurrentPlayer()
            drawDiceUseCase(repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet)

            if(repository.gameState.value.currentPlayerUuid != repository.myUuidState.value) {
                playOpponentTurnUseCase { triggerDiceRowAnimation() }
            }
        }
    }

    fun onScoreAndRollAgain() {
        repository.sumRoundPoints()

        scope.launch {
            repository.hideSelectedDice()
            delay(200L)
            triggerDiceRowAnimation()

            if(repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet.all { !it.isVisible }) {
                repository.resetDiceState()
            }

            drawDiceUseCase(repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet)
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
                            delay(2000L)

                            repository.toggleSkucha()
                            repository.resetRoundAndSelectedPoints()

                            triggerDiceRowAnimation()

                            repository.resetDiceState()
                            repository.changeCurrentPlayer()

                            if(repository.gameState.value.currentPlayerUuid != repository.myUuidState.value) {
                                playOpponentTurnUseCase { triggerDiceRowAnimation() }
                            }
                        }
                    }
                }
        }
    }

    private suspend fun triggerDiceRowAnimation() {
        delay(200L)
        _isDiceAnimating.value = true
        delay(500L)
        SoundPlayer.playSound(SoundType.DICE_ROLLING)
        delay(500L)
        _isDiceAnimating.value = false
    }
}