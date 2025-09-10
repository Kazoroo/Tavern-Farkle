package pl.kazoroo.tavernFarkle.game.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.model.Player
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import java.util.UUID

class LocalGameRepository: GameRepository {
    private val _gameState = MutableStateFlow<GameState>(
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

    val currentPlayerIndex: Int
        get() = _gameState.value.getCurrentPlayerIndex()

    val currentPlayer: Player
        get() = _gameState.value.players[currentPlayerIndex]

    override fun setMyUuid(uuid: UUID) {
        _myUuidState.value = uuid
    }

    /**
     * Saves provided data as the current game state. Used to initialize game data.
     * @param gameState state to save.
     */
    override fun saveGameState(gameState: GameState) {
        _gameState.value = gameState
    }

    /**
     * Toggles the selection state of a dice at the specified index.
     * @param index The index of the dice to toggle the selection state for.
     */
    override fun toggleDiceSelection(index: Int) {
        val state = _gameState.value

        val updatedPlayers = state.players.toMutableList()
        val updatedDiceSet = updatedPlayers[currentPlayerIndex].diceSet.toMutableList()

        updatedDiceSet[index] = updatedDiceSet[index].copy(isSelected = !updatedDiceSet[index].isSelected)
        updatedPlayers[currentPlayerIndex] = updatedPlayers[currentPlayerIndex].copy(diceSet = updatedDiceSet)

        _gameState.update { it.copy(players = updatedPlayers) }
    }

    /**
     * Updates the selected points state for the current player.
     * @param selectedPoints The new selected points value.
     */
    override fun updateSelectedPoints(selectedPoints: Int) {
        _gameState.update { state ->
            val playersWithUpdatedPoints = state.players.toMutableList().apply {
                this[currentPlayerIndex] = this[currentPlayerIndex].copy(selectedPoints = selectedPoints)
            }

            state.copy(players = playersWithUpdatedPoints)
        }
    }

    /**
     * Assign selected and round points to the current player round points state. Reset selected points.
     */
    override fun sumRoundPoints() {
        _gameState.update { state ->
            val roundPoints = state.players[currentPlayerIndex].roundPoints
            val selectedPoints = state.players[currentPlayerIndex].selectedPoints

            val playersWithUpdatedPoints = state.players.toMutableList().apply {
                this[currentPlayerIndex] = this[currentPlayerIndex].copy(
                    roundPoints = roundPoints + selectedPoints,
                    selectedPoints = 0
                )
            }

            state.copy(players = playersWithUpdatedPoints)
        }
    }

    /**
     * Updates the dice visibility state for the current player based on their selected state.
     */
    override fun hideSelectedDice() {
        val updatedDiceList = currentPlayer.diceSet.map { dice ->
            if (dice.isSelected) dice.copy(isVisible = false, isSelected = false)
            else dice
        }

        val playersWithUpdatedDice = _gameState.value.players.toMutableList().apply {
            this[currentPlayerIndex] = this[currentPlayerIndex].copy(diceSet = updatedDiceList)
        }

        _gameState.update { state ->
            state.copy(players = playersWithUpdatedDice)
        }
    }

    /**
     * Assigns given dice list to the current player dice set state.
     * @param newDice The new dice set to be assigned to the current player state.
     */
    override fun updateDiceSet(newDice: List<Dice>) {
        val updatedDiceSet = currentPlayer.diceSet.toMutableList().apply {
            clear()
            addAll(newDice)
        }

        val playersWithUpdatedDice = _gameState.value.players.toMutableList().apply {
            this[currentPlayerIndex] = this[currentPlayerIndex].copy(diceSet = updatedDiceSet)
        }

        _gameState.update { it.copy(players = playersWithUpdatedDice) }
    }

    /**
     * Assign selected, round and total points to the total current player points. Reset selected and round points.
     */
    override fun sumTotalPoints() {
        _gameState.update { state ->
            val totalPoints = state.players[currentPlayerIndex].totalPoints
            val roundPoints = state.players[currentPlayerIndex].roundPoints
            val selectedPoints = state.players[currentPlayerIndex].selectedPoints

            val playersWithUpdatedPoints = state.players.toMutableList().apply {
                this[currentPlayerIndex] = this[currentPlayerIndex].copy(
                    totalPoints = totalPoints + roundPoints + selectedPoints,
                    roundPoints = 0,
                    selectedPoints = 0
                )
            }

            state.copy(players = playersWithUpdatedPoints)
        }
    }

    /**
     * Resets to default the dice selection and visibility state for the current player.
     */
    override fun resetDiceState() {
        _gameState.update {
            val updatedPlayers = it.players.toMutableList().apply {
                this[currentPlayerIndex] = this[currentPlayerIndex].copy(
                    diceSet = this[currentPlayerIndex].diceSet.map { dice ->
                        dice.copy(
                            isSelected = false,
                            isVisible = true
                        )
                    }
                )
            }

            it.copy(players = updatedPlayers)
        }
    }

    /**
     * Updates the state of current player uuid to the next player.
     */
    override fun changeCurrentPlayer() {
        _gameState.update { state ->
            state.copy(
                currentPlayerUuid = state.players.first { it.uuid != state.currentPlayerUuid }.uuid
            )
        }
    }
}