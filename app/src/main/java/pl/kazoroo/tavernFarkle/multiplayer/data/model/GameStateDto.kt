package pl.kazoroo.tavernFarkle.multiplayer.data.model

import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import java.util.UUID

data class GameStateDto(
    val gameUuid: String = "",
    val betAmount: Int = 0,
    val isSkucha: Boolean = false,
    val currentPlayerUuid: String = "",
    val isGameEnd: Boolean = false,
    val players: List<PlayerDto> = emptyList()
) {
    fun getCurrentPlayerIndex(): Int {
        return players.indexOfFirst { it.uuid == currentPlayerUuid }
    }

    fun toDomain() = GameState(
        gameUuid = UUID.fromString(gameUuid),
        betAmount = betAmount,
        isSkucha = isSkucha,
        currentPlayerUuid = UUID.fromString(currentPlayerUuid),
        isGameEnd = isGameEnd,
        players = players.map { it.toDomain() }
    )
}