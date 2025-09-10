package pl.kazoroo.tavernFarkle.game.presentation.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase

class GameViewModelRefactor(
    private val repository: GameRepository,
    private val calculatePointsUseCase: CalculatePointsUseCase
): ViewModel() {
    val gameState: StateFlow<GameState> = repository.gameState

    fun toggleDiceSelection(index: Int) {
        repository.toggleDiceSelection(index)
        val currentPlayerDiceSet = gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet
        calculatePointsUseCase(currentPlayerDiceSet)
    }

    fun onPass() {
        if(!gameState.value.isSkucha) {
            repository.passTheRound()
            //repository.sumTotalPoints()
        }
    }

    fun onScoreAndRollAgain() {
        repository.sumRoundPoints()
        repository.hideSelectedDice()
    }
}