package pl.kazoroo.tavernFarkle.core.data.local.repository

import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

object SpecialDiceList {
    val specialDiceList = listOf(
        SpecialDice(
            name = SpecialDiceName.ODD_DICE,
            price = 150,
            image = listOf(R.drawable.odd_dice_1, R.drawable.odd_dice_2, R.drawable.odd_dice_3, R.drawable.odd_dice_4, R.drawable.odd_dice_5, R.drawable.odd_dice_6),
            chancesOfDrawingValue = listOf(26.7f, 6.7f, 26.7f, 6.7f, 26.7f, 6.7f),
        ),
        SpecialDice(
            name = SpecialDiceName.ALFONSES_DICE,
            price = 450,
            image = listOf(R.drawable.alfonses_dice_1, R.drawable.alfonses_dice_2, R.drawable.alfonses_dice_3, R.drawable.alfonses_dice_4, R.drawable.alfonses_dice_5, R.drawable.alfonses_dice_6),
            chancesOfDrawingValue = listOf(38.5f, 7.7f, 7.7f, 7.7f, 15.4f, 23f),
        ),
        SpecialDice(
            name = SpecialDiceName.SPIDERS_DICE,
            price = 300,
            image = listOf(R.drawable.spiders_dice_1, R.drawable.spiders_dice_2, R.drawable.spiders_dice_3, R.drawable.spiders_dice_4, R.drawable.spiders_dice_5, R.drawable.spiders_dice_6),
            chancesOfDrawingValue = listOf(18.2f, 22.7f, 45.5f, 4.5f, 4.5f, 4.5f),
        ),
        SpecialDice(
            name = SpecialDiceName.ROYAL_DICE,
            price = 3,
            image = listOf(R.drawable.royal_dice_1, R.drawable.royal_dice_2, R.drawable.royal_dice_3, R.drawable.royal_dice_4, R.drawable.royal_dice_5, R.drawable.royal_dice_6),
            chancesOfDrawingValue = listOf(25f, 12.5f, 12.5f, 12.5f, 18.8f,	18.8f),
        ),
    )
}