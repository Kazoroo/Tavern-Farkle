package pl.kazoroo.tavernFarkle.game.domain.repository

import kotlinx.coroutines.flow.StateFlow
import pl.kazoroo.tavernFarkle.game.domain.model.GameState

interface GameRepository {
    val gameState: StateFlow<GameState>
    fun saveGameState(gameState: GameState)
    fun toggleDiceSelection(index: Int)
    fun passTheRound()
    fun scoreAndRollAgain()
}