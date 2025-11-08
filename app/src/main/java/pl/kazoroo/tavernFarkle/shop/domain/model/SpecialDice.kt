package pl.kazoroo.tavernFarkle.shop.domain.model

import androidx.annotation.DrawableRes

data class SpecialDice(
    val name: SpecialDiceName,
    val price: Int,
    @param:DrawableRes val image: List<Int>,
    val chancesOfDrawingValue: List<Float>
) {
    init {
        require(chancesOfDrawingValue.size == 6) { "chancesOfDrawingValue must contain 6 elements" }
        require(price > 0) { "price must be greater than 0" }
    }
}
