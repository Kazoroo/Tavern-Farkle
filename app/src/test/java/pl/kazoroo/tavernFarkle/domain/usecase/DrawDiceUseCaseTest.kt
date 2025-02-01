package pl.kazoroo.tavernFarkle.domain.usecase

import org.junit.Test
import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class DrawDiceUseCaseTest {
    val ownedSpecialDice = listOf(
        OwnedSpecialDice(
            name = SpecialDiceName.ODD_DICE,
            count = 4,
            isSelected = listOf(true, true, true, false)
        ),
        OwnedSpecialDice(
            name = SpecialDiceName.ALFONSES_DICE,
            count = 5,
            isSelected = listOf(true, true, true, false, false)
        )
    )


    @Test
    fun `simulate 2 throws of dice`() {
        val useCase = DrawDiceUseCase()

        val firstThrowResult = useCase.invoke(
            ownedSpecialDices = ownedSpecialDice,
            usedSpecialDice = listOf(),
            isDiceVisible = List(6) { true }
        )

        val secondThrowResult = useCase.invoke(
            ownedSpecialDices = ownedSpecialDice,
            usedSpecialDice = listOf(SpecialDiceName.ODD_DICE),
            isDiceVisible = List(5) { true } + false
        )

        assert(firstThrowResult.all { it.specialDiceName != null })
        assert(secondThrowResult.size == 6)
        assert(secondThrowResult.count { it.specialDiceName == SpecialDiceName.ODD_DICE } == 2)
        assert(secondThrowResult.count { it.specialDiceName == SpecialDiceName.ALFONSES_DICE } == 3)
        assert(secondThrowResult.count { it.specialDiceName == null } == 1)
        assert(secondThrowResult.find { it.specialDiceName == null }!!.value == 0)
    }

    @Test
    fun `4 specials and 2 regular dice`() {
        val useCase = DrawDiceUseCase()
        val fourSpecialDice = listOf(
            OwnedSpecialDice(
                name = SpecialDiceName.ODD_DICE,
                count = 4,
                isSelected = listOf(false, false, false, true)
            ),
            OwnedSpecialDice(
                name = SpecialDiceName.ALFONSES_DICE,
                count = 4,
                isSelected = listOf(false, true, true, true)
            )
        )

        val throwResult = useCase.invoke(
            ownedSpecialDices = fourSpecialDice,
            usedSpecialDice = emptyList(),
            isDiceVisible = List(6) { true }
        )

        assert(throwResult.count { it.specialDiceName == SpecialDiceName.ODD_DICE } == 1)
        assert(throwResult.count { it.specialDiceName == SpecialDiceName.ALFONSES_DICE } == 3)
        assert(throwResult.count { it.specialDiceName == null } == 2)
        assert(throwResult.count { it.value == 0 } == 0)
    }
}