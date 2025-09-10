package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.model.Player
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import java.util.UUID

class StartNewGameUseCase(
    private val gameRepository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
) {
    operator fun invoke(betAmount: Int) {
        val currentPlayerId = UUID.randomUUID()
        val players = listOf(
            Player(
                uuid = currentPlayerId,
                diceSet = drawDiceUseCase.invoke()
            ),
            Player(
                uuid = UUID.randomUUID(),
                diceSet = drawDiceUseCase.invoke()
            ),
        )

        val gameState = GameState(
            betAmount = betAmount,
            gameUuid = UUID.randomUUID(),
            isSkucha = false,
            currentPlayerUuid = currentPlayerId,
            players = players,
            isGameEnd = false,
        )

        gameRepository.saveGameState(gameState)
    }
}