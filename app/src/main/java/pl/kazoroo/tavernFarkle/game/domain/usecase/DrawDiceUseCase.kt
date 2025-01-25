package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.repository.SpecialDiceList
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import kotlin.random.Random

class DrawDiceUseCase {
    private val diceDrawables = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6
    )

    operator fun invoke(ownedSpecialDices: List<OwnedSpecialDice>): List<Dice> {
        val selectedDiceNames = ownedSpecialDices.flatMap { dice ->
            dice.isSelected.mapIndexedNotNull { _, isSelected ->
                if (isSelected) dice.name else null
            }
        }

        //TODO: handle opponents special dice -> draw random amount of random special dice type and use them for drawing opponents dice
        //FIXME: when user select special dice and roll again it shouldn't appear again
        return constructRandomDiceListWithSpecials(selectedDiceNames).shuffled()
    }

    /**
     * Constructs a list of 6 Dice, including special dice specified by name, and filling any remaining slots with regular dice.
     *
     * @param isSelectedNameList A list of SpecialDiceName representing the special dice to be included.
     * @return A list of 6 Dice objects, containing the specified special dice and regular dice if needed.
     */
    private fun constructRandomDiceListWithSpecials(
        isSelectedNameList: List<SpecialDiceName>
    ): List<Dice> {
        val finalList: MutableList<Dice> = mutableListOf()

        isSelectedNameList.forEach { name ->
            val index = SpecialDiceList.specialDiceList.indexOfFirst { it.name == name }
            val value =
                getRandomWithProbability(SpecialDiceList.specialDiceList[index].chancesOfDrawingValue)

            finalList.add(
                Dice(
                    value = value,
                    image = SpecialDiceList.specialDiceList[index].image[value - 1]
                )
            )
        }

        if (isSelectedNameList.count() < 6) {
            val remainingCount = 6 - isSelectedNameList.count()

            repeat(remainingCount) {
                val diceIndex = Random.nextInt(until = 6)

                finalList.add(
                    Dice(
                        value = diceIndex + 1,
                        image = diceDrawables[diceIndex]
                    )
                )
            }
        }
        return finalList
    }

    private fun getRandomWithProbability(probabilities: List<Float>): Int {
        val total = probabilities.sum()
        val normalizedProbabilities = probabilities.map { it / total }

        val cumulative = normalizedProbabilities.runningFold(0.0) { acc, probability -> acc + probability }.drop(1)
        val randomValue = Random.nextDouble(0.0, 1.0)

        return cumulative.indexOfFirst { randomValue <= it } + 1
    }
}
