package pl.kazoroo.tavernFarkle.shop.domain.model

import androidx.annotation.DrawableRes

data class SpecialDice(
    val name: String,
    val price: Int,
    @DrawableRes val image: Int,
    val chancesOfDrawingValue: List<Float>
)
