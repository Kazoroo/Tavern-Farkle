package pl.kazoroo.tavernFarkle.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.PlayerStatus

interface GameRepository {
    val gameState: StateFlow<GameState>
    val myUuidState: StateFlow<String>
    fun setMyUuid(uuid: String)
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
    fun setGameEnd(gameEnd: Boolean)
    fun getMyPlayerIndex(): Int
    fun getOpponentPlayerIndex(): Flow<Int?>
    fun removeLobbyNode()
    fun toggleDiceRowAnimation()
    fun updatePlayerStatus(status: PlayerStatus)
}