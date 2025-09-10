package pl.kazoroo.tavernFarkle.game.domain.model

import java.util.UUID

data class Player(
    val uuid: UUID,
    val totalPoints: Int = 0,
    val roundPoints: Int = 0,
    val selectedPoints: Int = 0,
    val diceSet: List<Dice>
) {
    init {
        require(diceSet.size == 6) { "Game rules enforce DiceSet to have exactly 6 dice" }
    }
}
