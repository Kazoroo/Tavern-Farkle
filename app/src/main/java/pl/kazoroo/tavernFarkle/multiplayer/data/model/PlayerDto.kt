package pl.kazoroo.tavernFarkle.multiplayer.data.model

import pl.kazoroo.tavernFarkle.core.domain.model.Player

data class PlayerDto(
    val uuid: String = "",
    val totalPoints: Int = 0,
    val roundPoints: Int = 0,
    val selectedPoints: Int = 0,
    val diceSet: List<DiceDto> = emptyList()
) {
    fun toDomain() = Player(
        uuid = uuid,
        totalPoints = totalPoints,
        roundPoints = roundPoints,
        selectedPoints = selectedPoints,
        diceSet = diceSet.map { it.toDomain() }
    )
}