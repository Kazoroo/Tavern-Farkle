package pl.kazoroo.tavernFarkle.multiplayer.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.FirebaseDataSource
import java.util.UUID

class RemoteGameRepository(
    private val firebaseDataSource: FirebaseDataSource
) : GameRepository {
    private val _gameState = MutableStateFlow(
        GameState(
            betAmount = 0,
            gameUuid = UUID.randomUUID(),
            isSkucha = false,
            currentPlayerUuid = UUID.randomUUID(),
            players = emptyList(),
        )
    )
    override val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _myUuidState = MutableStateFlow<UUID>(UUID.randomUUID())
    override val myUuidState: StateFlow<UUID> = _myUuidState.asStateFlow()

    override fun setMyUuid(uuid: UUID) {
        _myUuidState.value = uuid
    }

    override fun saveGameState(gameState: GameState) {
        _gameState.value = gameState

        firebaseDataSource.setGameState(gameState)
    }

    override fun updateSelectedPoints(selectedPoints: Int) {
    }

    override fun toggleDiceSelection(index: Int) {

    }

    override fun sumRoundPoints() {
        TODO("Not yet implemented")
    }

    override fun hideSelectedDice() {
        TODO("Not yet implemented")
    }

    override fun sumTotalPoints() {
        TODO("Not yet implemented")
    }

    override fun updateDiceSet(newDice: List<Dice>) {
        TODO("Not yet implemented")
    }

    override fun resetDiceState() {
        TODO("Not yet implemented")
    }

    override fun changeCurrentPlayer() {
        TODO("Not yet implemented")
    }

    override fun resetRoundAndSelectedPoints() {
        TODO("Not yet implemented")
    }

    override fun toggleSkucha() {
        TODO("Not yet implemented")
    }

    override fun toggleGameEnd() {
        TODO("Not yet implemented")
    }
}