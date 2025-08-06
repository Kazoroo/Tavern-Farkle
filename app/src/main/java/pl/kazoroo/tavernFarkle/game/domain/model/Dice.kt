package pl.kazoroo.tavernFarkle.game.domain.model

import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

data class Dice(
    val value: Int,
    val isSelected: Boolean = false,
    val isVisible: Boolean = true,
    val specialDiceName: SpecialDiceName? = null,
    val image: Int
)
