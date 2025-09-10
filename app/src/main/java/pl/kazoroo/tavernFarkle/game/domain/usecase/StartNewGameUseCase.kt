package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.model.Player
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
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
                diceSet = drawDiceUseCase.invoke(
                    listOf(
                        Dice(value = 0, image = 0, specialDiceName = null),
                        Dice(value = 0, image = 0, specialDiceName = null),
                        Dice(value = 0, image = 0, specialDiceName = SpecialDiceName.SPIDERS_DICE),
                        Dice(value = 0, image = 0, specialDiceName = null),
                        Dice(value = 0, image = 0, specialDiceName = SpecialDiceName.ROYAL_DICE),
                        Dice(value = 0, image = 0, specialDiceName = null),
                    )
                )
            ),
            Player(
                uuid = UUID.randomUUID(),
                diceSet = drawDiceUseCase.invoke(
                    listOf(
                        Dice(value = 0, image = 0, specialDiceName = null),
                        Dice(value = 0, image = 0, specialDiceName = null),
                        Dice(value = 0, image = 0, specialDiceName = SpecialDiceName.SPIDERS_DICE),
                        Dice(value = 0, image = 0, specialDiceName = null),
                        Dice(value = 0, image = 0, specialDiceName = SpecialDiceName.ROYAL_DICE),
                        Dice(value = 0, image = 0, specialDiceName = null),
                    )
                )
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