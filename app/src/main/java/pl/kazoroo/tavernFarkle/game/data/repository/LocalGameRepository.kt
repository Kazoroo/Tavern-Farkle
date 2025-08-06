package pl.kazoroo.tavernFarkle.game.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        TODO("Not yet implemented")
    }

    override fun passTheRound() {
        TODO("Not yet implemented")
    }

    override fun scoreAndRollAgain() {
        TODO("Not yet implemented")
    }
}