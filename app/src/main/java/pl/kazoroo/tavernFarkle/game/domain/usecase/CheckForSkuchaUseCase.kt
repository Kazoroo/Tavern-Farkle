package pl.kazoroo.tavernFarkle.game.domain.usecase

import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository

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
        diceList: List<Dice>,
        repository: GameRepository
    ): Boolean {
        val diceListWithVisibleSelected = diceList.map {
            if(it.isVisible) it.copy(isSelected = true)
            else it
        }

        val points = calculatePointsUseCase(
            diceList = diceListWithVisibleSelected,
            isCheckingForSkucha = true,
            repository = repository
        )

        return points == 0
    }
}