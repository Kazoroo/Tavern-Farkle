package pl.kazoroo.tavernFarkle.multiplayer.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.kazoroo.tavernFarkle.core.domain.GameStateUpdater
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.multiplayer.data.model.Lobby
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.FirebaseDataSource
import java.util.UUID

class RemoteGameRepository(
    val firebaseDataSource: FirebaseDataSource,
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

    private val _myUuidState = MutableStateFlow<UUID>(UUID.randomUUID())
    override val myUuidState: StateFlow<UUID> = _myUuidState.asStateFlow()

    private val _lobbyList = MutableStateFlow<List<Lobby>>(emptyList())
    val lobbyList: StateFlow<List<Lobby>> = _lobbyList.asStateFlow()

    override fun setMyUuid(uuid: UUID) {
        _myUuidState.value = uuid
    }

    override fun saveGameState(gameState: GameState) {
        _gameState.value = updater.saveGameState(gameState)
    }

    fun saveGameDataToDatabase(gameState: GameState) {
        firebaseDataSource.setGameState(gameState)
    }

    override fun toggleDiceSelection(index: Int) {
        _gameState.update { updater.toggleDiceSelection(it, index) }

        firebaseDataSource.updateDiceSelection(
            gameUuid = gameState.value.gameUuid.toString(),
            playerIndex = gameState.value.getCurrentPlayerIndex(),
            index = index,
            value = gameState.value.getCurrentPlayer().diceSet[index].isSelected
        )
    }

    override fun updateSelectedPoints(selectedPoints: Int) {
        _gameState.update { updater.updateSelectedPoints(it, selectedPoints) }

        firebaseDataSource.updateSelectedPoints(
            gameUuid = gameState.value.gameUuid.toString(),
            playerIndex = gameState.value.getCurrentPlayerIndex(),
            value = gameState.value.getCurrentPlayer().selectedPoints
        )
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

    fun observeLobbyList() {
        firebaseDataSource.observeLobbyList { lobbies ->
            _lobbyList.value = lobbies
        }
    }

    fun observeGameData(gameState: GameState) {
        firebaseDataSource.observeGameData(gameState.gameUuid.toString()) { players ->
            _gameState.update { updater.updatePlayers(gameState, players) }
        }
    }
}