package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.game.domain.model.Dice

class CheckForSkuchaUseCase {
    /**
     * Check if user can get any points, if not there is skucha.
     *
     * @param diceList List of values for each of the six dice
     * @param isDiceVisible List of booleans indication whether the dice at a given index is visible for the user
     * @return true if there is skucha
     */
    operator fun invoke(
        diceList: List<Dice>,
        isDiceVisible: List<Boolean>
    ): Boolean {
        val points = CalculatePointsUseCase().invoke(
            diceList = diceList,
            isDiceSelected = isDiceVisible,
            includeNonScoringDice = false
        )

        return points == 0
    }
}