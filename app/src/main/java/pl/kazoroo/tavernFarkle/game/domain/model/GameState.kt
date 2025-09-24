package pl.kazoroo.tavernFarkle.game.domain.model

import pl.kazoroo.tavernFarkle.multiplayer.data.model.GameStateDto
import java.util.UUID

data class GameState(
    val gameUuid: UUID,
    val betAmount: Int,
    val isSkucha: Boolean = false,
    val currentPlayerUuid: UUID,
    val isGameEnd: Boolean = false,
    val players: List<Player>
) {
    fun getCurrentPlayerIndex(): Int {
        return players.indexOfFirst { it.uuid == currentPlayerUuid }
    }

    fun toDto() = GameStateDto(
        gameUuid = gameUuid.toString(),
        betAmount = betAmount,
        isSkucha = isSkucha,
        currentPlayerUuid = currentPlayerUuid.toString(),
        isGameEnd = isGameEnd,
        players = players.map { it.toDto() }
    )
}