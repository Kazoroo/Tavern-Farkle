package pl.kazoroo.tavernFarkle.core.domain.model

import pl.kazoroo.tavernFarkle.multiplayer.data.model.PlayerDto
import java.util.UUID

data class Player(
    val uuid: UUID,
    val totalPoints: Int = 0,
    val roundPoints: Int = 0,
    val selectedPoints: Int = 0,
    val diceSet: List<Dice>
) {
    init {
        require(diceSet.size == 6) { "Game rules enforce diceSet to have exactly 6 dice" }
    }

    fun toDto() = PlayerDto(
        uuid = uuid.toString(),
        totalPoints = totalPoints,
        roundPoints = roundPoints,
        selectedPoints = selectedPoints,
        diceSet = diceSet.map { it.toDto() }
    )
}
