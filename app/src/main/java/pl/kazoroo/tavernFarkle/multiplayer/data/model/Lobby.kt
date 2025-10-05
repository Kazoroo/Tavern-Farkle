package pl.kazoroo.tavernFarkle.multiplayer.data.model

data class Lobby(
    val gameUuid: String = "",
    val betAmount: Int = 0,
    val playerCount: Int = 0
)