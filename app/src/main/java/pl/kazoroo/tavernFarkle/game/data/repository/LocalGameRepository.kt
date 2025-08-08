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
            val updatedPoints = state.players.toMutableList().apply {
                this[currentPlayerIndex] = this[currentPlayerIndex].copy(selectedPoints = selectedPoints)
            }

            state.copy(players = updatedPoints)
        }
    }


    override fun passTheRound() {
        TODO("Not yet implemented")
    }

    override fun scoreAndRollAgain() {
        TODO("Not yet implemented")
    }
}