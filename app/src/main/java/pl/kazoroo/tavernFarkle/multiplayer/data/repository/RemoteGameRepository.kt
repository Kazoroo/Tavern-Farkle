package pl.kazoroo.tavernFarkle.multiplayer.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
            currentPlayerUuid = "",
            players = emptyList(),
        )
    )
    override val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _myUuidState = MutableStateFlow("")
    override val myUuidState: StateFlow<String> = _myUuidState.asStateFlow()

    private val _lobbyList = MutableStateFlow<List<Lobby>>(emptyList())
    val lobbyList: StateFlow<List<Lobby>> = _lobbyList.asStateFlow()

    /**
     * Return an index of the player that is assigned to this client
     *
     * @return index of the player in the current game state that matches this client's UUID or `null` if not found
     */
    override fun getMyPlayerIndex(): Int =
        _gameState.value.players.indexOfFirst { it.uuid == myUuidState.value }
            .takeIf { it >= 0 } ?: 0

    /**
     * Returns a [Flow] that emits the index of the opponent player relative to this client.
     * The value updates reactively whenever the players list changes.
     *
     * @return a [Flow] emitting the index of the opponent player, or `null` if no opponent is found
     */
    override fun getOpponentPlayerIndex(): Flow<Int?> {
        return _gameState.map { state ->
            state.players.indexOfFirst { it.uuid != myUuidState.value }
                .takeIf { it >= 0 }
        }
    }

    override fun setMyUuid(uuid: String) {
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

        firebaseDataSource.updatePlayers(
            gameUuid = gameState.value.gameUuid.toString(),
            playerIndex = gameState.value.getCurrentPlayerIndex(),
            value = gameState.value.getCurrentPlayer().toDto()
        )
    }

    override fun hideSelectedDice() {
        _gameState.update { updater.hideSelectedDice(it) }

        firebaseDataSource.updatePlayers(
            gameUuid = gameState.value.gameUuid.toString(),
            playerIndex = gameState.value.getCurrentPlayerIndex(),
            value = gameState.value.getCurrentPlayer().toDto()
        )
    }

    override fun updateDiceSet(newDice: List<Dice>) {
        _gameState.update { updater.updateDiceSet(it, newDice) }

        firebaseDataSource.updatePlayers(
            gameUuid = gameState.value.gameUuid.toString(),
            playerIndex = gameState.value.getCurrentPlayerIndex(),
            value = gameState.value.getCurrentPlayer().toDto()
        )
    }

    override fun sumTotalPoints() {
        _gameState.update { updater.sumTotalPoints(it) }

        firebaseDataSource.updatePlayers(
            gameUuid = gameState.value.gameUuid.toString(),
            playerIndex = gameState.value.getCurrentPlayerIndex(),
            value = gameState.value.getCurrentPlayer().toDto()
        )
    }

    override fun resetDiceState() {
        _gameState.update { updater.resetDiceState(it) }

        firebaseDataSource.updatePlayers(
            gameUuid = gameState.value.gameUuid.toString(),
            playerIndex = gameState.value.getCurrentPlayerIndex(),
            value = gameState.value.getCurrentPlayer().toDto()
        )
    }

    override fun changeCurrentPlayer() {
        _gameState.update { updater.changeCurrentPlayer(it) }

        firebaseDataSource.updateCurrentPlayerUuid(
            gameUuid = gameState.value.gameUuid.toString(),
            value = gameState.value.currentPlayerUuid
        )
    }

    override fun resetRoundAndSelectedPoints() {
        _gameState.update { updater.resetRoundAndSelectedPoints(it) }

        firebaseDataSource.updatePlayers(
            gameUuid = gameState.value.gameUuid.toString(),
            playerIndex = gameState.value.getCurrentPlayerIndex(),
            value = gameState.value.getCurrentPlayer().toDto()
        )
    }

    override fun toggleSkucha() {
        _gameState.update { updater.toggleSkucha(it) }
        firebaseDataSource.setGameState(_gameState.value)
    }

    override fun setGameEnd(gameEnd: Boolean) {
        _gameState.update { updater.setGameEnd(it, gameEnd) }
        firebaseDataSource.setGameState(_gameState.value)
    }

    fun observeLobbyList() {
        firebaseDataSource.observeLobbyList { lobbies ->
            _lobbyList.value = lobbies.sortedBy { it.playerCount }
        }
    }

    fun observeGameData(gameState: GameState) {
        firebaseDataSource.observeGameData(
            gameUuid = gameState.gameUuid.toString(),
            onUpdate = { gameStateDto ->
                val newState = gameStateDto?.toDomain() ?: return@observeGameData
                _gameState.value = newState
            }
        )
    }

    override fun removeLobbyNode() {
        firebaseDataSource.removeLobbyNode(
            gameUuid = gameState.value.gameUuid.toString()
        )
    }
}