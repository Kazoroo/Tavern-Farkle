package pl.kazoroo.tavernFarkle.domain.usecase

import org.junit.Test
import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase

class DrawDiceUseCaseTest {

    @Test
    fun `check if there are 6 dice`() {
        val result = DrawDiceUseCase(inventoryViewModel.ownedSpecialDice.value).invoke()

        assert(result.size == 6)
    }

    @Test
    fun `check if dice values are in appropriate range`() {
        val result = DrawDiceUseCase(inventoryViewModel.ownedSpecialDice.value).invoke()

        val areValuesInRange = result.map { it.value.coerceIn(1..6) == it.value }

        assert(areValuesInRange.all { it })
    }
}