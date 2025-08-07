package pl.kazoroo.tavernFarkle.game.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository

class GameViewModelRefactor(
    private val repository: GameRepository
): ViewModel() {
    val gameState: StateFlow<GameState> = repository.gameState

    fun toggleDiceSelection(index: Int) {
        repository.toggleDiceSelection(index)
    }

    fun onPass() {
        if(!gameState.value.isSkucha) {
            viewModelScope.launch {
                repository.passTheRound()
            }
        }
    }

    fun onScoreAndRollAgain() {
        viewModelScope.launch {
            repository.scoreAndRollAgain()
        }
    }
}