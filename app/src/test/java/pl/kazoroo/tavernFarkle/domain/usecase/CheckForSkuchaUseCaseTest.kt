package pl.kazoroo.tavernFarkle.domain.usecase

import org.junit.Test
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.usecase.CheckForSkuchaUseCase

class CheckForSkuchaUseCaseTest {

    @Test
    fun `check for skucha when all dice are visible and there are available points`() {
        val result = CheckForSkuchaUseCase().invoke(
            diceList = listOf(
                Dice(null,1, 0),
                Dice(null,2, 0),
                Dice(null,3, 0),
                Dice(null,4, 0),
                Dice(null,4, 0),
                Dice(null,6, 0)
            ),
            isDiceVisible = listOf(
                true,
                true,
                true,
                true,
                true,
                true,
            )
        )

        assert(!result)
    }

    @Test
    fun `check for skucha when all dice are visible and there are no available points`() {
        val result = CheckForSkuchaUseCase().invoke(
            diceList = listOf(
                Dice(null,6, 0),
                Dice(null,2, 0),
                Dice(null,3, 0),
                Dice(null,4, 0),
                Dice(null,4, 0),
                Dice(null,6, 0)
            ),
            isDiceVisible = listOf(
                true,
                true,
                true,
                true,
                true,
                true,
            )
        )

        assert(result)
    }

    @Test
    fun `check for skucha when three dice are visible and there are no available points`() {
        val result = CheckForSkuchaUseCase().invoke(
            diceList = listOf(
                Dice(null,6, 0),
                Dice(null,2, 0),
                Dice(null,3, 0),
                Dice(null,1, 0),
                Dice(null,1, 0),
                Dice(null,1, 0)
            ),
            isDiceVisible = listOf(
                true,
                true,
                true,
                false,
                false,
                false,
            )
        )

        assert(result)
    }
}