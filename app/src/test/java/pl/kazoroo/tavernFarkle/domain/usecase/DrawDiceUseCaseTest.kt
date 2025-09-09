package pl.kazoroo.tavernFarkle.domain.usecase

import org.junit.Before
import org.junit.Test
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.game.data.repository.LocalGameRepository
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.CheckForSkuchaUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.StartNewGameUseCase
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import kotlin.test.assertEquals

class DrawDiceUseCaseTest {
    lateinit var repository: LocalGameRepository
    lateinit var drawDiceUseCase: DrawDiceUseCase
    private val diceDrawables = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6
    )

    @Before
    fun setUp() {
        repository = LocalGameRepository()
        val calculatePointsUseCase = CalculatePointsUseCase(repository)
        val checkForSkuchaUseCase = CheckForSkuchaUseCase(calculatePointsUseCase)
        drawDiceUseCase = DrawDiceUseCase(repository, checkForSkuchaUseCase)
    }

    @Test
    fun `invoke with normal dice`() {
        val diceSet = List(6) { Dice(value = 3, image = 0, specialDiceName = null) }
        val result = drawDiceUseCase(diceSet)

        assert(result.size == 6)
        assert(result.all { it.value in 1..6 })
        assert(result.all { it.image == diceDrawables[it.value - 1] })
        assert(result.all { it.specialDiceName == null })
    }

    @Test
    fun `check repository value and returned value are equal`() {
        val diceSet = List(6) { Dice(value = 3, image = 0, specialDiceName = null) }

        StartNewGameUseCase(repository, drawDiceUseCase).invoke(0, emptyList())
        val returnedDiceSet = drawDiceUseCase(diceSet)

        assert(repository.gameState.value.players.isNotEmpty())
        assertEquals(returnedDiceSet, repository.gameState.value.players[0].diceSet)
    }

    @Test
    fun `useCase return values with proper probability`() {
        val diceSet = List(6) { Dice(value = 3, image = 0, specialDiceName = SpecialDiceName.SPIDERS_DICE) }
        val values = (1..10_000).flatMap { drawDiceUseCase(diceSet).map { it.value } }

        assertProbability(values, 3, 0.455f)
        assertProbability(values, 1, 0.182f)
        assertProbability(values, 5, 0.045f)
    }

    private fun assertProbability(valueList: List<Int>, target: Int, expected: Float) {
        val count = valueList.count { it == target }
        val percent = count.toFloat() / valueList.size
        assert(percent in (expected - 0.02f)..(expected + 0.02f)) {
            "Value $target was $percent, expected around $expected"
        }
    }

    @Test
    fun `test that only value and image change`() {
        val diceSet = listOf(
            Dice(value = 3, image = 2, specialDiceName = null, isVisible = false),
            Dice(value = 5, image = 4, specialDiceName = null),
            Dice(value = 1, image = 0, specialDiceName = SpecialDiceName.ROYAL_DICE),
            Dice(value = 1, image = 0, specialDiceName = null, isSelected = true),
            Dice(value = 6, image = 5, specialDiceName = null),
            Dice(value = 2, image = 1, specialDiceName = SpecialDiceName.ALFONSES_DICE),
        )

        val result = drawDiceUseCase(diceSet)

        assert(result.count { it.isSelected == true } == 1)
        assert(result.count { it.isVisible == false } == 1)
        assert(result.count { it.specialDiceName == SpecialDiceName.ROYAL_DICE } == 1)
        assert(result.count { it.specialDiceName == SpecialDiceName.ALFONSES_DICE } == 1)
    }
}