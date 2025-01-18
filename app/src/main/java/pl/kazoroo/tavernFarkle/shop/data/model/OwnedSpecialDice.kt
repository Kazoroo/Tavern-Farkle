package pl.kazoroo.tavernFarkle.shop.data.model

import kotlinx.serialization.Serializable
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

@Serializable
data class OwnedSpecialDice(
    val name: SpecialDiceName = SpecialDiceName.ODD_DICE,
    val count: Int = 0,
    val isSelected: List<Boolean> = List(count) { false }
) {
    init {
        require(isSelected.size == count) { "isSelected size must be equal to count" }
    }
}

