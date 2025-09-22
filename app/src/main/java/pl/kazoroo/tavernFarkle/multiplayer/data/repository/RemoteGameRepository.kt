package pl.kazoroo.tavernFarkle.multiplayer.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.model.Player
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import java.util.UUID

class RemoteGameRepository : GameRepository {
    private val _gameState = MutableStateFlow(
        GameState(
            betAmount = 0,
            gameUuid = UUID.randomUUID(),
            isSkucha = false,
            currentPlayerUuid = UUID.randomUUID(),
            players = List(2) {
                Player(
                    uuid = UUID.randomUUID(),
                    diceSet = List(6) { Dice(image = 1, value = 1) },
                    selectedPoints = 0,
                    roundPoints = 0,
                    totalPoints = 0
                )
            },
        )
    )
    override val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _myUuidState = MutableStateFlow<UUID>(UUID.randomUUID())
    override val myUuidState: StateFlow<UUID> = _myUuidState.asStateFlow()

    override fun setMyUuid(uuid: UUID) {
        TODO("Not yet implemented")
    }

    override fun saveGameState(gameState: GameState) {
        TODO("Not yet implemented")
    }

    override fun updateSelectedPoints(selectedPoints: Int) {
        TODO("Not yet implemented")
    }

    override fun toggleDiceSelection(index: Int) {
        println("RemoteGameRepository answer!")
    }

    override fun sumRoundPoints() {
        TODO("Not yet implemented")
    }

    override fun hideSelectedDice() {
        TODO("Not yet implemented")
    }

    override fun sumTotalPoints() {
        TODO("Not yet implemented")
    }

    override fun updateDiceSet(newDice: List<Dice>) {
        TODO("Not yet implemented")
    }

    override fun resetDiceState() {
        TODO("Not yet implemented")
    }

    override fun changeCurrentPlayer() {
        TODO("Not yet implemented")
    }

    override fun resetRoundAndSelectedPoints() {
        TODO("Not yet implemented")
    }

    override fun toggleSkucha() {
        TODO("Not yet implemented")
    }

    override fun toggleGameEnd() {
        TODO("Not yet implemented")
    }
}