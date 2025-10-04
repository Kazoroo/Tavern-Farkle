package pl.kazoroo.tavernFarkle.singleplayer.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import pl.kazoroo.tavernFarkle.core.domain.GameStateUpdater
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import java.util.UUID

class LocalGameRepository(
    private val updater: GameStateUpdater
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

    private val _myUuidState = MutableStateFlow(UUID.randomUUID())
    override val myUuidState: StateFlow<UUID> = _myUuidState.asStateFlow()

    override fun getMyPlayerIndex(): Int {
        return 0
    }

    override fun getOpponentPlayerIndex(): Flow<Int?> {
        return flowOf(1)
    }

    override fun setMyUuid(uuid: UUID) {
        _myUuidState.value = uuid
    }

    override fun saveGameState(gameState: GameState) {
        _gameState.value = updater.saveGameState(gameState)
    }

    override fun toggleDiceSelection(index: Int) {
        _gameState.update { updater.toggleDiceSelection(it, index) }
    }

    override fun updateSelectedPoints(selectedPoints: Int) {
        _gameState.update { updater.updateSelectedPoints(it, selectedPoints) }
    }

    override fun sumRoundPoints() {
        _gameState.update { updater.sumRoundPoints(it) }
    }

    override fun hideSelectedDice() {
        _gameState.update { updater.hideSelectedDice(it) }
    }

    override fun updateDiceSet(newDice: List<Dice>) {
        _gameState.update { updater.updateDiceSet(it, newDice) }
    }

    override fun sumTotalPoints() {
        _gameState.update { updater.sumTotalPoints(it) }
    }

    override fun resetDiceState() {
        _gameState.update { updater.resetDiceState(it) }
    }

    override fun changeCurrentPlayer() {
        _gameState.update { updater.changeCurrentPlayer(it) }
    }

    override fun resetRoundAndSelectedPoints() {
        _gameState.update { updater.resetRoundAndSelectedPoints(it) }
    }

    override fun toggleSkucha() {
        _gameState.update { updater.toggleSkucha(it) }
    }

    override fun toggleGameEnd() {
        _gameState.update { updater.toggleGameEnd(it) }
    }
}
