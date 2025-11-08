package pl.kazoroo.tavernFarkle.multiplayer.data.model

import androidx.annotation.Keep
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import java.util.UUID

@Keep
data class GameStateDto(
    val gameUuid: String = "",
    val betAmount: Int = 0,
    val skucha: Boolean = false,
    val currentPlayerUuid: String = "",
    val animating: Boolean = false,
    val gameEnd: Boolean = false,
    val players: List<PlayerDto> = emptyList()
) {
    fun toDomain() = GameState(
        gameUuid = UUID.fromString(gameUuid),
        betAmount = betAmount,
        isSkucha = skucha,
        isAnimating = animating,
        currentPlayerUuid = currentPlayerUuid,
        isGameEnd = gameEnd,
        players = players.map { it.toDomain() }
    )
}