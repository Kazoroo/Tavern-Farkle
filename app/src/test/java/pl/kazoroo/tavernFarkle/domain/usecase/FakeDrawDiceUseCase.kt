package pl.kazoroo.tavernFarkle.domain.usecase

import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CheckForSkuchaUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase

class FakeDrawDiceUseCase(private val repository: GameRepository) : DrawDiceUseCase(
    repository,
    CheckForSkuchaUseCase(CalculatePointsUseCase(repository))
) {
    private var callCounter = 0
    private val normalDiceSet = listOf(2, 2, 3, 4, 3, 6)

    override operator fun invoke(diceSet: List<Dice>): List<Dice> {
        val currentDiceSet = if (repository.gameState.value.players.isEmpty())
            List(6) { Dice(value = 1, image = 0) }
            else repository.gameState.value.players[0].diceSet
        val modifierDiceSet = currentDiceSet.mapIndexed { index, dice ->
            dice.copy(
                value = when (callCounter) {
                    0 -> {
                        1
                    }
                    1, 2 -> {
                        5
                    }
                    else -> {
                        normalDiceSet[index]
                    }
                }
            )
        }

        if(repository.gameState.value.players.isNotEmpty()) repository.updateDiceSet(modifierDiceSet)
        callCounter++

        return modifierDiceSet
    }
}