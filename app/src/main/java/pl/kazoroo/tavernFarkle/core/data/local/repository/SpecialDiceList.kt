package pl.kazoroo.tavernFarkle.core.data.local.repository

import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.shop.data.model.SpecialDiceName
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice

object SpecialDiceList {
    val specialDiceList = listOf(
        SpecialDice(
            name = SpecialDiceName.ODD_DICE,
            price = 2,
            image = R.drawable.odd_dice_1,
            chancesOfDrawingValue = listOf(26.7f, 6.7f, 26.7f, 6.7f, 26.7f, 6.7f),
        ),
        SpecialDice(
            name = SpecialDiceName.ALFONSES_DICE,
            price = 1000,
            image = R.drawable.alfonses_dice_1,
            chancesOfDrawingValue = listOf(38.5f, 7.7f, 7.7f, 7.7f, 15.4f, 23f),
        ),
    )
}