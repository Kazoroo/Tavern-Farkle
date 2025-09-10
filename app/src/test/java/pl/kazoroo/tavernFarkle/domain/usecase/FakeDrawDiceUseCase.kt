package pl.kazoroo.tavernFarkle.domain.usecase

import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase

class FakeDrawDiceUseCase(private val repository: GameRepository) : DrawDiceUseCase(repository) {
    private var callCounter = 0
    val diceSet2 = listOf(
        Dice(value = 5, image = 0),
        Dice(value = 5, image = 0),
        Dice(value = 5, image = 0),
        Dice(value = 5, image = 0),
        Dice(value = 5, image = 0),
        Dice(value = 5, image = 0)
    )

    override operator fun invoke(): List<Dice> {
        val diceSet1 = listOf(
            Dice(value = 1, image = 0),
            Dice(value = 1, image = 0),
            Dice(value = 1, image = 0),
            Dice(value = 1, image = 0),
            Dice(value = 1, image = 0),
            Dice(value = 1, image = 0)
        )

        val diceSetToReturn = if(callCounter == 0) diceSet1 else diceSet2
        if(repository.gameState.value.players.isNotEmpty()) repository.updateDiceSet(diceSetToReturn)
        callCounter++
        return diceSetToReturn
    }
}