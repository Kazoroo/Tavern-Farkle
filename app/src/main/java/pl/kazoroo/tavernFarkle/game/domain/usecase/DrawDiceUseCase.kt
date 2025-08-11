package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.repository.SpecialDiceList
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import kotlin.random.Random

class DrawDiceUseCase(
    private val repository: GameRepository
) {
    private val diceDrawables = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6
    )

    private val opponentSpecialDiceList = List(Random.nextInt(until = 5)) {
        SpecialDiceList.specialDiceList.random().name
    }
    /*
        /**
         * Constructs a list of 6 dice objects, including special dice specified by name, and filling any remaining slots with regular dice.
         *
         * @param ownedSpecialDices list of all special dice owned by the user
         * @param usedSpecialDice a list of special dice used during the game, meaning that they will not be used in further draws
         * @param isDiceVisible list of booleans indicating whether each dice should be visible or not
         *
         * @return A list of 6 Dice objects, containing the specified special dice and regular dice if needed, in random order.
         */

        operator fun invoke(
            ownedSpecialDices: List<OwnedSpecialDice>,
            usedSpecialDice: List<SpecialDiceName>,
            isDiceVisible: List<Boolean>,
            isOpponentTurn: Boolean
        ): List<Dice> {
            if(isOpponentTurn) {
                val notUsedSpecialDice = opponentSpecialDiceList.toMutableList().apply {
                    usedSpecialDice.forEach { used ->
                        remove(used)
                    }
                }

                return constructRandomDiceListWithSpecials(notUsedSpecialDice, isDiceVisible)
            } else {
                val inGameSpecialDiceNames = ownedSpecialDices.flatMap { dice ->
                    dice.isSelected.mapIndexedNotNull { _, isSelected ->
                        if (isSelected) dice.name else null
                    }
                }

                val notUsedSpecialDice = inGameSpecialDiceNames.toMutableList().apply {
                    usedSpecialDice.forEach { used ->
                        remove(used)
                    }
                }

                return constructRandomDiceListWithSpecials(notUsedSpecialDice, isDiceVisible)
            }
        }
    */

    operator fun invoke(): List<Dice> {
        val newDice = List(6) {
            val value = Random.nextInt(until = 6)

            Dice(
                value = value + 1,
                image = diceDrawables[value]
            )
        }
        if(repository.gameState.value.players.isNotEmpty()) repository.updateDiceSet(newDice)

        return newDice
    }

    /**
     * Constructs a list of 6 Dice, including special dice specified by name, and filling any remaining slots with regular dice.
     *
     * @param activeSpecialDice A list of SpecialDiceName representing the special dice to be included.
     * @return A list of 6 Dice objects, containing the specified special dice and regular dice if needed.
     */
    private fun constructRandomDiceListWithSpecials(
        activeSpecialDice: List<SpecialDiceName>,
        isDiceVisible: List<Boolean>
    ): List<Dice> {
        val finalList: MutableList<Dice> = MutableList(6) { Dice(value = 0, image = diceDrawables[0]) }
        val visibleDiceIndex = isDiceVisible.mapIndexedNotNull { itemIndex, isVisible ->
            if(isVisible) itemIndex else null
        }.shuffled()

        activeSpecialDice.forEachIndexed { index, name  ->
            val specialDice = SpecialDiceList.specialDiceList.first { it.name == name }
            val value = getRandomWithProbability(specialDice.chancesOfDrawingValue)

            finalList.set(
                index = visibleDiceIndex[index],
                element = Dice(
                    specialDiceName = specialDice.name,
                    value = value,
                    image = specialDice.image[value - 1]
                )
            )
        }

        val isSpaceForNormalDice = activeSpecialDice.count() < isDiceVisible.count { it }
        if (isSpaceForNormalDice) {
            val remainingCount = 6 - activeSpecialDice.count()
            val unfilledPositions = finalList.mapIndexedNotNull { index, dice ->
                if(dice.specialDiceName == null) index else null
            }.shuffled()

            repeat(remainingCount) { index ->
                val diceIndex = Random.nextInt(until = 6)

                finalList.set(
                    index = unfilledPositions[index],
                    element = Dice(
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

        val cumulative = normalizedProbabilities
            .runningFold(0.0) { acc, probability -> acc + probability }
            .drop(1)
        val randomValue = Random.nextDouble(0.0, 1.0)

        return cumulative.indexOfFirst { randomValue <= it } + 1
    }
}
