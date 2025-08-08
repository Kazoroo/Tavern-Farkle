package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository

class CalculatePointsUseCase(
    private val repository: GameRepository,
) {
    operator fun invoke(
        diceList: List<Dice>,
        includeNonScoringDice: Boolean = true
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
            points = calculatePointsForSingleDiceValues(occurrencesMap, points)

            nonScoringDice = occurrencesMap.keys.filter { value ->
                (value != 1 && value != 5 && (occurrencesMap[value] ?: 0) < 3)
            }
        }

        val isAvailablePoints = nonScoringDice.isEmpty() || !includeNonScoringDice

        val selectedPoints = if(isAvailablePoints) points else 0
        repository.savePoints(selectedPoints)

        return selectedPoints
    }

    private fun calculatePointsForSingleDiceValues(
        occurrencesMap: Map<Int, Int>,
        points: Int
    ): Int {
        var singleDiceValuePoints = points

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
