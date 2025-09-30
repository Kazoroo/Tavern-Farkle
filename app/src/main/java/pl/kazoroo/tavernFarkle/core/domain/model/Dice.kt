package pl.kazoroo.tavernFarkle.core.domain.model

import pl.kazoroo.tavernFarkle.multiplayer.data.model.DiceDto
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

data class Dice(
    val value: Int,
    val isSelected: Boolean = false,
    val isVisible: Boolean = true,
    val specialDiceName: SpecialDiceName? = null,
    val image: Int
) {
    fun toDto() = DiceDto(
        value = value,
        selected = isSelected,
        visible = isVisible,
        specialDiceName = specialDiceName,
        image = image
    )
}
