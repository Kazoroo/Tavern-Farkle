package pl.kazoroo.tavernFarkle.core.domain.model

import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

data class Dice(
    val value: Int = 0,
    val isSelected: Boolean = false,
    val isVisible: Boolean = true,
    val specialDiceName: SpecialDiceName? = null,
    val image: Int = 0
)
