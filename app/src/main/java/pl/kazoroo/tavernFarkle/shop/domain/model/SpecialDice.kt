package pl.kazoroo.tavernFarkle.shop.domain.model

import androidx.annotation.DrawableRes
import pl.kazoroo.tavernFarkle.shop.data.model.SpecialDiceName

data class SpecialDice(
    val name: SpecialDiceName,
    val price: Int,
    @DrawableRes val image: Int,
    val chancesOfDrawingValue: List<Float>
)
