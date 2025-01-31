package pl.kazoroo.tavernFarkle.game.domain.usecase

import android.util.Log
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
        usedSpecialDice: List<SpecialDiceName> = listOf(),
        isDiceVisible: List<Boolean> = List(6) { true }

    ): List<Dice> {
        println("ARGUMENTS: ")
        println("ownedSpecialDices: ${ownedSpecialDices.joinToString { "\n\t" + it.toString()}}")
        println("usedSpecialDice: $usedSpecialDice")
        println("isDiceVisible: $isDiceVisible")
        println("--ARGUMENTS END-----------")
        println(" ")

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

        //TODO: handle opponents special dice -> draw random amount of random special dice type and use them for drawing opponents dice
        return constructRandomDiceListWithSpecials(notUsedSpecialDice, isDiceVisible)
    }

    /**
     * Constructs a list of 6 Dice, including special dice specified by name, and filling any remaining slots with regular dice.
     *
     * @param availableInGameSpecialDiceNames A list of SpecialDiceName representing the special dice to be included.
     * @return A list of 6 Dice objects, containing the specified special dice and regular dice if needed.
     */
    private fun constructRandomDiceListWithSpecials(
        availableInGameSpecialDiceNames: List<SpecialDiceName>,
        isDiceVisible: List<Boolean>
    ): List<Dice> {
        val finalList: MutableList<Dice> = MutableList(6) { Dice(value = 0, image = diceDrawables[0]) }

        availableInGameSpecialDiceNames.forEachIndexed { index, name  ->
            val specialDiceTypeIndex = SpecialDiceList.specialDiceList.indexOfFirst { it.name == name }
            val value = getRandomWithProbability(SpecialDiceList.specialDiceList[specialDiceTypeIndex].chancesOfDrawingValue)
            val visibleDiceIndex = isDiceVisible.mapIndexedNotNull { itemIndex, isVisible ->
                if(isVisible) itemIndex else null
            }
            val invisibleDiceIndex = isDiceVisible.mapIndexedNotNull { itemIndex, isVisible ->
                if(!isVisible) itemIndex else null
            }
            val isDiceVisibleSorted = visibleDiceIndex + invisibleDiceIndex

            finalList.set(
                index = isDiceVisibleSorted[index],
                element = Dice(
                    specialDiceName = SpecialDiceList.specialDiceList[specialDiceTypeIndex].name,
                    value = value,
                    image = SpecialDiceList.specialDiceList[specialDiceTypeIndex].image[value - 1]
                )
            )
        }

        println("finalList before adding nonSpecials: ${finalList.joinToString {  "\n" + it.toString()}}")
        println("\n")
/*        finalList before adding nonSpecials:
        Dice(specialDiceName=ODD_DICE, value=6, image=2131165278),
        Dice(specialDiceName=ODD_DICE, value=5, image=2131165277),
        Dice(specialDiceName=null, value=0, image=2131165226),
        Dice(specialDiceName=null, value=0, image=2131165226),
        Dice(specialDiceName=null, value=0, image=2131165226),
        Dice(specialDiceName=null, value=0, image=2131165226)*/

        Log.d("DiceListBuilder", "availableInGameSpecialDiceNames: $availableInGameSpecialDiceNames")
        Log.d("DiceListBuilder","how much dice are visible: ${isDiceVisible.count { it }}")
        /*if (availableInGameSpecialDiceNames.count() < isDiceVisible.count { it }) { // 2 < 5
            val remainingCount = 6 - availableInGameSpecialDiceNames.count()
            val unfilledPositions = finalList.mapIndexedNotNull { index, dice ->
                if(dice.specialDiceName != null) index else null
            }
            Log.d("DiceListBuilder", "remainingCount: $remainingCount") // 4
            Log.d("DiceListBuilder","unfilledPositions: $unfilledPositions") // [0, 1]

            repeat(remainingCount) { index ->
                val diceIndex = Random.nextInt(until = 6)

                finalList.set(
                    index = unfilledPositions[index], //IOOB 1 for 1
                    element = Dice(
                        value = diceIndex + 1,
                        image = diceDrawables[diceIndex]
                    )
                )
            }
        }*/
        println("finalList: ${finalList.joinToString {  "\n" + it.toString()}}")
        println("\n")
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
