package pl.kazoroo.tavernFarkle.core.domain.model

import pl.kazoroo.tavernFarkle.multiplayer.data.model.GameStateDto
import java.util.UUID

data class GameState(
    val gameUuid: UUID,
    val hostConnected: Boolean = true,
    val betAmount: Int,
    val isSkucha: Boolean = false,
    val isAnimating: Boolean = false,
    val currentPlayerUuid: String,
    val isGameEnd: Boolean = false,
    val targetScore: Int = DEFAULT_TARGET_SCORE,
    val players: List<Player>
) {
    fun getCurrentPlayerIndex(): Int = players.indexOfFirst { it.uuid == currentPlayerUuid }
    fun getCurrentPlayer(): Player = players[getCurrentPlayerIndex()]

    fun toDto() = GameStateDto(
        gameUuid = gameUuid.toString(),
        hostConnected = hostConnected,
        betAmount = betAmount,
        skucha = isSkucha,
        animating = isAnimating,
        currentPlayerUuid = currentPlayerUuid,
        gameEnd = isGameEnd,
        targetScore = targetScore,
        players = players.map { it.toDto() }
    )

    companion object {
        const val DEFAULT_TARGET_SCORE = 4000
    }
}
