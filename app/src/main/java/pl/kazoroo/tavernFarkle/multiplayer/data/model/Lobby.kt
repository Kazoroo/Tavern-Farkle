package pl.kazoroo.tavernFarkle.multiplayer.data.model

import androidx.annotation.Keep
import pl.kazoroo.tavernFarkle.core.domain.model.GameState

@Keep
data class Lobby(
    val gameUuid: String = "",
    val betAmount: Int = 0,
    val playerCount: Int = 0,
    val targetScore: Int = GameState.DEFAULT_TARGET_SCORE
)