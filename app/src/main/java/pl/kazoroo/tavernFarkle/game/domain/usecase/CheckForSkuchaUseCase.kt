package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.game.domain.model.Dice

class CheckForSkuchaUseCase(
    private val calculatePointsUseCase: CalculatePointsUseCase
) {
    /**
     * Check if user can get any points, if not there is skucha.
     *
     * @param diceList List of values for each of the six dice
     * @return true if there is skucha
     */
    operator fun invoke(
        diceList: List<Dice>
    ): Boolean {
        val points = calculatePointsUseCase(
            diceList = diceList,
            includeNonScoringDice = false
        )

        return points == 0
    }
}