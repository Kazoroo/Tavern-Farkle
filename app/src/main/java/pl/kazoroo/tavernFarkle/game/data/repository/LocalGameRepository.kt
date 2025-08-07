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
        val currentState = _gameState.value
        val currentPlayerIndex = currentState.getCurrentPlayerIndex()
        val currentPlayer = currentState.players[currentPlayerIndex]

        val updatedDiceSet = currentPlayer.diceSet.toMutableList().also {
            val old = it[index]
            it[index] = old.copy(isSelected = !old.isSelected)
        }

        val updatedPlayers = currentState.players.toMutableList().also {
            it[currentPlayerIndex] = currentPlayer.copy(diceSet = updatedDiceSet)
        }

        _gameState.update { it.copy(players = updatedPlayers) }
    }

    override fun passTheRound() {
        TODO("Not yet implemented")
    }

    override fun scoreAndRollAgain() {
        TODO("Not yet implemented")
    }
}