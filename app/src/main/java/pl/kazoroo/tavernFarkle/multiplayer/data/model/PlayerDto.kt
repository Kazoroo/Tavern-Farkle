package pl.kazoroo.tavernFarkle.multiplayer.data.model

import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.Player
import java.util.UUID

data class PlayerDto(
    val uuid: String = "",
    val totalPoints: Int = 0,
    val roundPoints: Int = 0,
    val selectedPoints: Int = 0,
    val diceSet: List<Dice> = emptyList()
) {
    fun toDomain() = Player(
        uuid = UUID.fromString(uuid),
        totalPoints = totalPoints,
        roundPoints = roundPoints,
        selectedPoints = selectedPoints,
        diceSet = diceSet
    )
}
