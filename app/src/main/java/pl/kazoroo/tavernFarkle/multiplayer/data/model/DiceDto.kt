package pl.kazoroo.tavernFarkle.multiplayer.data.model

import androidx.annotation.Keep
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

@Keep
data class DiceDto(
    val value: Int = 0,
    val selected: Boolean = false,
    val visible: Boolean = true,
    val specialDiceName: SpecialDiceName? = null,
    val image: Int = 0
) {
    fun toDomain() = Dice(
        value = value,
        isSelected = selected,
        isVisible = visible,
        specialDiceName = specialDiceName,
        image = image
    )
}
