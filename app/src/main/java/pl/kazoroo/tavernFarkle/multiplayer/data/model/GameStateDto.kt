package pl.kazoroo.tavernFarkle.multiplayer.data.model

import androidx.annotation.Keep
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import java.util.UUID

@Keep
data class GameStateDto(
    val gameUuid: String = "",
    val hostConnected: Boolean = true,
    val betAmount: Int = 0,
    val skucha: Boolean = false,
    val currentPlayerUuid: String = "",
    val animating: Boolean = false,
    val gameEnd: Boolean = false,
    val targetScore: Int = GameState.DEFAULT_TARGET_SCORE,
    val players: List<PlayerDto> = emptyList()
) {
    fun toDomain(): GameState? {

        if(gameUuid.isBlank()) return null

        return GameState(
            gameUuid = UUID.fromString(gameUuid),
            hostConnected = hostConnected,
            betAmount = betAmount,
            isSkucha = skucha,
            isAnimating = animating,
            currentPlayerUuid = currentPlayerUuid,
            isGameEnd = gameEnd,
            targetScore = targetScore,
            players = players.map { it.toDomain() }
        )
    }
}
