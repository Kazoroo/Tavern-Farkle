package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository

class CalculatePointsUseCase() {
    /**
     * Calculates selected points and update state. Includes sequences and single dice values.
     *
     * @param diceList Current player list of values to get selected dice.
     * @param isCheckingForSkucha used for calculating points for skucha. Bypass some rules of normal CalculatePointsUseCase behavior to get proper result.
     *
     * @return maximum number of points user can select.
     */
    operator fun invoke(
        diceList: List<Dice>,
        isCheckingForSkucha: Boolean = false,
        repository: GameRepository,

        ): Int {
        val valuesOfSelectedDicesList: List<Int> = diceList.map { dice ->
            if (dice.isSelected) dice.value else 0
        }.filter { it > 0 }

        val occurrencesMap: Map<Int, Int> = valuesOfSelectedDicesList.groupingBy { it }.eachCount()
        var points = 0
        var nonScoringDice: List<Int> = listOf()

        when (valuesOfSelectedDicesList.sorted()) {
            listOf(1, 2, 3, 4, 5, 6) -> points += 1500
            listOf(2, 3, 4, 5, 6) -> points += 750
            listOf(2, 3, 4, 5, 5, 6) -> points += 800
            listOf(1, 2, 3, 4, 5) -> points += 500
            listOf(1, 2, 3, 4, 5, 5) -> points += 550
            listOf(1, 1, 2, 3, 4, 5) -> points += 600
        }

        if(points == 0) {
            points = calculatePointsForSingleDiceValues(occurrencesMap)

            nonScoringDice = occurrencesMap.keys.filter { value ->
                (value != 1 && value != 5 && (occurrencesMap[value] ?: 0) < 3)
            }
        }

        val isAvailablePoints = nonScoringDice.isEmpty() || isCheckingForSkucha

        val selectedPoints = if(isAvailablePoints) points else 0

        if(!isCheckingForSkucha) repository.updateSelectedPoints(selectedPoints)
        return selectedPoints
    }

    private fun calculatePointsForSingleDiceValues(
        occurrencesMap: Map<Int, Int>
    ): Int {
        var singleDiceValuePoints = 0

        occurrencesMap.forEach { (value, count) ->
            when (value) {
                1 -> {
                    singleDiceValuePoints += if (count < 3) {
                        100 * count
                    } else {
                        1000 * (count - 2)
                    }
                }

                5 -> {
                    singleDiceValuePoints += if (count < 3) {
                        50 * count
                    } else {
                        500 * (count - 2)
                    }
                }

                else -> {
                    singleDiceValuePoints += if (count >= 3) {
                        value * 100 * (count - 2)
                    } else {
                        0
                    }
                }
            }
        }

        return singleDiceValuePoints
    }
}
