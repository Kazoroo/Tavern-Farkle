package pl.kazoroo.tavernFarkle.game.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.PlayOpponentTurnUseCase

class GameViewModelRefactor(
    private val repository: GameRepository,
    private val calculatePointsUseCase: CalculatePointsUseCase,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val playOpponentTurnUseCase: PlayOpponentTurnUseCase
): ViewModel() {
    val gameState: StateFlow<GameState> = repository.gameState

    val isOpponentTurn: StateFlow<Boolean> =
        gameState.map { state ->
            state.currentPlayerUuid != repository.myUuidState.value
        }.stateIn(viewModelScope, SharingStarted.Lazily, false)


    fun toggleDiceSelection(index: Int) {
        repository.toggleDiceSelection(index)
        val currentPlayerDiceSet = gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet
        calculatePointsUseCase(currentPlayerDiceSet)
    }

    fun onPass() {
        repository.sumTotalPoints()
        repository.resetDiceState()
        repository.changeCurrentPlayer()
        drawDiceUseCase(repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet)

        if(repository.gameState.value.currentPlayerUuid != repository.myUuidState.value) {
            viewModelScope.launch {
                playOpponentTurnUseCase()
            }
        }
    }

    fun onScoreAndRollAgain() {
        repository.sumRoundPoints()
        repository.hideSelectedDice()
        drawDiceUseCase(repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet)
    }
}