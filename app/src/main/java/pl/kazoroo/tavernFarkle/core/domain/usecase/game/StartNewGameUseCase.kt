package pl.kazoroo.tavernFarkle.core.domain.usecase.game

import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.model.Player
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import java.util.UUID

class StartNewGameUseCase(
    private val gameRepository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
) {
    operator fun invoke(
        betAmount: Int,
        userDiceNames: List<SpecialDiceName>,
        isMultiplayer: Boolean
    ) {
        val currentPlayerId = UUID.randomUUID()
        val paddedUserDiceNames = userDiceNames.padWithNullsToSix()
        val userDiceSet = createDiceSet(paddedUserDiceNames, gameRepository, drawDiceUseCase)

        val players = if(isMultiplayer) {
            listOf(Player(currentPlayerId, diceSet = userDiceSet))
        } else {
            val opponentDiceNames: List<SpecialDiceName?> = List(
                (userDiceNames.size..userDiceNames.size + 1).random()
            ) {
                SpecialDiceName.entries.toTypedArray().random()
            }.padWithNullsToSix()

            listOf(
                Player(currentPlayerId, diceSet = userDiceSet),
                Player(UUID.randomUUID(), diceSet = createDiceSet(opponentDiceNames, gameRepository, drawDiceUseCase))
            )
        }

        val currentSkuchaStatus = gameRepository.gameState.value.isSkucha
        val gameState = GameState(
            betAmount = betAmount,
            gameUuid = UUID.randomUUID(),
            isSkucha = currentSkuchaStatus,
            currentPlayerUuid = currentPlayerId,
            players = players,
            isGameEnd = false,
        )

        gameRepository.saveGameState(gameState)
        gameRepository.setMyUuid(currentPlayerId)
    }
}

fun createDiceSet(specialDiceNames: List<SpecialDiceName?>, gameRepository: GameRepository, drawDiceUseCase: DrawDiceUseCase) =
    drawDiceUseCase(
        List(6) { index ->
            Dice(value = 0, image = 0, specialDiceName = specialDiceNames[index])
        },
        repository = gameRepository
    )


fun List<SpecialDiceName?>.padWithNullsToSix(): List<SpecialDiceName?> =
    take(6) + List((6 - size).coerceAtLeast(0)) { null }