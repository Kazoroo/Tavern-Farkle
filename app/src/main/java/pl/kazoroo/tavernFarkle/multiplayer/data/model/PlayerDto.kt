package pl.kazoroo.tavernFarkle.multiplayer.data.model

import androidx.annotation.Keep
import pl.kazoroo.tavernFarkle.core.domain.model.Player
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.PlayerStatus

@Keep
data class PlayerDto(
    val uuid: String = "",
    val totalPoints: Int = 0,
    val roundPoints: Int = 0,
    val selectedPoints: Int = 0,
    val status: String = PlayerStatus.IN_GAME.name,
    val statusTimestamp: Long = 0,
    val diceSet: List<DiceDto> = emptyList()
) {
    fun toDomain() = Player(
        uuid = uuid,
        totalPoints = totalPoints,
        roundPoints = roundPoints,
        selectedPoints = selectedPoints,
        status = PlayerStatus.valueOf(status),
        statusTimestamp = statusTimestamp,
        diceSet = diceSet.map { it.toDomain() }
    )
}