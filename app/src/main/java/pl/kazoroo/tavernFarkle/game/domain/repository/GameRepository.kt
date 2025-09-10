package pl.kazoroo.tavernFarkle.game.domain.repository

import kotlinx.coroutines.flow.StateFlow
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import java.util.UUID

interface GameRepository {
    val gameState: StateFlow<GameState>
    val myUuidState: StateFlow<UUID>
    fun setMyUuid(uuid: UUID)
    fun saveGameState(gameState: GameState)
    fun updateSelectedPoints(selectedPoints: Int)
    fun toggleDiceSelection(index: Int)
    fun sumRoundPoints()
    fun hideSelectedDice()
    fun sumTotalPoints()
    fun updateDiceSet(newDice: List<Dice>)
    fun resetDiceState()
    fun changeCurrentPlayer()
}