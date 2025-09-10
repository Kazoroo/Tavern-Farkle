package pl.kazoroo.tavernFarkle.game.domain.model

import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

data class Dice(
    val specialDiceName: SpecialDiceName? = null,
    val value: Int,
    val image: Int
)