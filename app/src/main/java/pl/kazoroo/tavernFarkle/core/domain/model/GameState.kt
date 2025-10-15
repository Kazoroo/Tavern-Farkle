package pl.kazoroo.tavernFarkle.core.domain.model

import pl.kazoroo.tavernFarkle.multiplayer.data.model.GameStateDto
import java.util.UUID

data class GameState(
    val gameUuid: UUID,
    val betAmount: Int,
    val isSkucha: Boolean = false,
    val currentPlayerUuid: String,
    val isGameEnd: Boolean = false,
    val players: List<Player>
) {
    fun getCurrentPlayerIndex(): Int =players.indexOfFirst { it.uuid == currentPlayerUuid }
    fun getCurrentPlayer(): Player = players[getCurrentPlayerIndex()]

    fun toDto() = GameStateDto(
        gameUuid = gameUuid.toString(),
        betAmount = betAmount,
        skucha = isSkucha,
        currentPlayerUuid = currentPlayerUuid,
        gameEnd = isGameEnd,
        players = players.map { it.toDto() }
    )
}