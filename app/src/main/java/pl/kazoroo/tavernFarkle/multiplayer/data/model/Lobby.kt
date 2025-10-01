package pl.kazoroo.tavernFarkle.multiplayer.data.model

import androidx.annotation.Keep

@Keep
data class Lobby(
    val gameUuid: String = "",
    val betAmount: Int = 0
)