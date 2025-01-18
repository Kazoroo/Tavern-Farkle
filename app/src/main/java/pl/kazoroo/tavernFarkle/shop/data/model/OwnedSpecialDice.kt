package pl.kazoroo.tavernFarkle.shop.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OwnedSpecialDice(
    val name: SpecialDiceName = SpecialDiceName.ODD_DICE,
    val count: Int = 0,
    val isSelected: List<Boolean> = listOf(false)
)
//TODO: make requirement that isSelected length same as count
enum class SpecialDiceName {
    ODD_DICE, ALFONSES_DICE
}