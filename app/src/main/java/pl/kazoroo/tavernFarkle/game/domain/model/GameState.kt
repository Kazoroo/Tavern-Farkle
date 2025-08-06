package pl.kazoroo.tavernFarkle.game.domain.model

import java.util.UUID

data class GameState(
    val gameUuid: UUID,
    val betAmount: Int,
    val isSkucha: Boolean = false,
    val currentPlayerUuid: UUID,
    val isGameEnd: Boolean = false,
    val players: List<Player>
)