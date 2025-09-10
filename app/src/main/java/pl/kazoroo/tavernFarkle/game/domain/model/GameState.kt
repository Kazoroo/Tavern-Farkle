package pl.kazoroo.tavernFarkle.game.domain.model

import java.util.UUID

data class GameState(
    val gameUuid: UUID,
    val betAmount: Int,
    val isSkucha: Boolean = false,
    val currentPlayerUuid: UUID,
    val players: List<Player>,
    val isGameEnd: Boolean = false
)