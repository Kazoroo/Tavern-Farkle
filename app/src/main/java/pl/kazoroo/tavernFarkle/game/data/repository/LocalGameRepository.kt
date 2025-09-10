package pl.kazoroo.tavernFarkle.game.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
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

    override fun saveGameState(gameState: GameState) {
        _gameState.value = gameState
    }

    override fun toggleDiceSelection(index: Int) {
        val state = _gameState.value
        val playerIndex = state.getCurrentPlayerIndex()

        val updatedPlayers = state.players.toMutableList()
        val updatedDiceSet = updatedPlayers[playerIndex].diceSet.toMutableList()

        updatedDiceSet[index] = updatedDiceSet[index].copy(isSelected = !updatedDiceSet[index].isSelected)
        updatedPlayers[playerIndex] = updatedPlayers[playerIndex].copy(diceSet = updatedDiceSet)

        _gameState.update { it.copy(players = updatedPlayers) }
    }

    override fun savePoints(selectedPoints: Int) {
        _gameState.update { state ->
            val currentPlayerIndex = state.getCurrentPlayerIndex()
            val playersWithUpdatedPoints = state.players.toMutableList().apply {
                this[currentPlayerIndex] = this[currentPlayerIndex].copy(selectedPoints = selectedPoints)
            }

            state.copy(players = playersWithUpdatedPoints)
        }
    }


    override fun sumRoundPoints() {
        _gameState.update { state ->
            val currentPlayerIndex = state.getCurrentPlayerIndex()
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

    override fun hideSelectedDice() {
        val currentPlayerIndex = _gameState.value.getCurrentPlayerIndex()
        val currentPlayer = _gameState.value.players[currentPlayerIndex]

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

    override fun passTheRound() {
        TODO("Not yet implemented")
    }
}