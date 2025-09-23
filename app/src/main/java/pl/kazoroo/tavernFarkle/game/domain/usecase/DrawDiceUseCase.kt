package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.repository.SpecialDiceList
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import kotlin.random.Random

open class DrawDiceUseCase(
    private val checkForSkuchaUseCase: CheckForSkuchaUseCase
) {
    private val diceDrawables = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6
    )
    private val normalDiceProbability = List(6) { 16.6f }

    /**
     * Generates new dice set with random values and images based on probability of drawing a specific value.
     * Updates the dice set in the repository.
     *
     * @param diceSet current dice set
     * @return shuffled dice set with new values
     */
    open operator fun invoke(
        diceSet: List<Dice>,
        repository: GameRepository
    ): List<Dice> {
        val newDiceSet = diceSet.map { dice ->
            val specialDice = dice.specialDiceName
                ?.let { name -> SpecialDiceList.specialDiceList.first { it.name == name } }
            val value = getRandomWithProbability(specialDice?.chancesOfDrawingValue ?: normalDiceProbability)

            dice.copy(
                value = value,
                image = (specialDice?.image ?: diceDrawables)[value - 1]
            )
        }
        val shuffledDiceSet = newDiceSet.shuffled()

        if(repository.gameState.value.players.isNotEmpty()) repository.updateDiceSet(shuffledDiceSet)
        val skucha = checkForSkuchaUseCase.invoke(
            shuffledDiceSet,
            repository = repository
        )
        if(skucha) repository.toggleSkucha()

        return shuffledDiceSet
    }

    private fun getRandomWithProbability(probabilities: List<Float>): Int {
        val total = probabilities.sum()
        val normalizedProbabilities = probabilities.map { it / total }

        val cumulative = normalizedProbabilities
            .runningFold(0.0) { acc, probability -> acc + probability }
            .drop(1)
        val randomValue = Random.nextDouble(0.0, 1.0)

        return cumulative.indexOfFirst { randomValue <= it } + 1
    }
}
