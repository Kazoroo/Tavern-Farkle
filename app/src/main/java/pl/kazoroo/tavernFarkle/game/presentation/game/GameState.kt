package pl.kazoroo.tavernFarkle.game.presentation.game

import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import java.util.UUID

data class GameState(
    val gameUuid: UUID,
    val betAmount: Int,
    val isSkucha: Boolean,
    val currentPlayerUuid: UUID,
    val players: List<Player>,
    val isGameEnd: Boolean
)

data class Player(
    val uuid: UUID,
    val totalPoints: Int,
    val roundPoints: Int,
    val selectedPoints: Int,
    val diceSet: List<Dice>
) {
    init {
        require(diceSet.size == 6) { "Game rules enforce DiceSet to have exactly 6 dice" }
    }
}

data class Dice(
    val value: Int,
    val isSelected: Boolean,
    val isVisible: Boolean,
    val special: SpecialDiceName? = null,
    val image: Int
)
