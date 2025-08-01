package pl.kazoroo.tavernFarkle.game.data.repository

import kotlinx.coroutines.flow.StateFlow
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.game.presentation.game.GameState

class LocalGameRepository: GameRepository {
    override val gameState: StateFlow<GameState>
        get() = TODO("Not yet implemented")

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