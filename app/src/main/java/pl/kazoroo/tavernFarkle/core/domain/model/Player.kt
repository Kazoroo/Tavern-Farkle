package pl.kazoroo.tavernFarkle.core.domain.model

import pl.kazoroo.tavernFarkle.multiplayer.data.model.PlayerDto
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.PlayerStatus

data class Player(
    val uuid: String,
    val totalPoints: Int = 0,
    val roundPoints: Int = 0,
    val selectedPoints: Int = 0,
    val status: PlayerStatus = PlayerStatus.IN_GAME,
    val statusTimestamp: Long = 0,
    val diceSet: List<Dice>
) {
    init {
        require(diceSet.size == 6) { "Game rules enforce diceSet to have exactly 6 dice" }
    }

    fun toDto() = PlayerDto(
        uuid = uuid,
        totalPoints = totalPoints,
        roundPoints = roundPoints,
        selectedPoints = selectedPoints,
        status = status.name,
        statusTimestamp = statusTimestamp,
        diceSet = diceSet.map { it.toDto() }
    )
}
