package pl.kazoroo.tavernFarkle.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
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
    fun resetRoundAndSelectedPoints()
    fun toggleSkucha()
    fun toggleGameEnd()
    fun getMyPlayerIndex(): Int
    fun getOpponentPlayerIndex(): Flow<Int?>
}