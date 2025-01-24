package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.repository.SpecialDiceList
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import kotlin.random.Random

class DrawDiceUseCase {
    operator fun invoke(ownedSpecialDices: List<OwnedSpecialDice>): List<Dice> {
        val diceDrawables = listOf(
            R.drawable.dice_1,
            R.drawable.dice_2,
            R.drawable.dice_3,
            R.drawable.dice_4,
            R.drawable.dice_5,
            R.drawable.dice_6
        )
        val isSelectedNameList = ownedSpecialDices.flatMap { dice ->
            dice.isSelected.mapIndexedNotNull { _, isSelected ->
                if (isSelected) dice.name else null
            }
        }
        val finalList: MutableList<Dice> = mutableListOf()

        isSelectedNameList.forEach { name ->
            val index = SpecialDiceList.specialDiceList.indexOfFirst { it.name == name }
            val value = getRandomWithProbability(SpecialDiceList.specialDiceList[index].chancesOfDrawingValue)

            finalList.add(
                Dice(
                    value = value,
                    image = SpecialDiceList.specialDiceList[index].image[value - 1]
                )
            )
        }

        if(isSelectedNameList.count() < 6) {
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

        //TODO: break the code to the smaller functions to increase readability
        //TODO: handle opponents special dice -> draw random amount of random special dice type and use them for drawing opponents dice
        //FIXME: when user select special dice and roll again it shouldn't appear again
        return finalList.shuffled()
    }

    private fun getRandomWithProbability(probabilities: List<Float>): Int {
        val total = probabilities.sum()
        val normalizedProbabilities = probabilities.map { it / total }

        val cumulative = normalizedProbabilities.runningFold(0.0) { acc, probability -> acc + probability }.drop(1)
        val randomValue = Random.nextDouble(0.0, 1.0)

        return cumulative.indexOfFirst { randomValue <= it } + 1
    }
}
