package pl.kazoroo.tavernFarkle.multiplayer.data.model

import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import java.util.UUID

data class GameStateDto(
    val gameUuid: String,
    val betAmount: Int,
    val isSkucha: Boolean = false,
    val currentPlayerUuid: String,
    val isGameEnd: Boolean = false,
    val players: List<PlayerDto>
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