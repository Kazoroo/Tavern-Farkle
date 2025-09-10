package pl.kazoroo.tavernFarkle.game.domain.repository

import kotlinx.coroutines.flow.StateFlow
import pl.kazoroo.tavernFarkle.game.domain.model.GameState

interface GameRepository {
    val gameState: StateFlow<GameState>
    fun saveGameState(gameState: GameState)
    fun savePoints(selectedPoints: Int)
    fun toggleDiceSelection(index: Int)
    fun sumRoundPoints()
    fun hideSelectedDice()
    fun passTheRound()
}