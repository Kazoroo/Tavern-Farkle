package pl.kazoroo.tavernFarkle.domain.usecase

import org.junit.Before
import org.junit.Test
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.model.Player
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CheckForSkuchaUseCase
import pl.kazoroo.tavernFarkle.singleplayer.data.repository.LocalGameRepository
import java.util.UUID
import kotlin.random.Random

class CheckForSkuchaUseCaseTest {
    lateinit var checkForSkuchaUseCase: CheckForSkuchaUseCase

    @Before
    fun initialize() {
        val repository = LocalGameRepository()

        val currentPlayerId = UUID.randomUUID()

        val diceList = List(6) {
            val value = Random.nextInt(until = 6)

            Dice(
                value = value + 1,
                image = R.drawable.dice_4
            )
        }

        val players = listOf(
            Player(
                uuid = currentPlayerId,
                diceSet = diceList
            ),
            Player(
                uuid = UUID.randomUUID(),
                diceSet = diceList
            ),
        )

        val gameState = GameState(
            betAmount = 200,
            gameUuid = UUID.randomUUID(),
            isSkucha = false,
            currentPlayerUuid = currentPlayerId,
            players = players,
            isGameEnd = false,
        )

        repository.saveGameState(gameState)

        val calculatePointsUseCase = CalculatePointsUseCase(repository)
        checkForSkuchaUseCase = CheckForSkuchaUseCase(calculatePointsUseCase)
    }

    @Test
    fun `check for skucha when all dice are visible and there are available points`() {
        val result = checkForSkuchaUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isVisible = true),
                Dice(value = 2, image = 0, isVisible = true),
                Dice(value = 3, image = 0, isVisible = true),
                Dice(value = 4, image = 0, isVisible = true),
                Dice(value = 4, image = 0, isVisible = true),
                Dice(value = 6, image = 0, isVisible = true),
            ),
        )

        assert(!result)
    }

    @Test
    fun `check for skucha when all dice are visible and there are no available points`() {
        val result = checkForSkuchaUseCase.invoke(
            diceList = listOf(
                Dice(value = 6, image = 0, isVisible = true),
                Dice(value = 2, image = 0, isVisible = true),
                Dice(value = 3, image = 0, isVisible = true),
                Dice(value = 4, image = 0, isVisible = true),
                Dice(value = 4, image = 0, isVisible = true),
                Dice(value = 6, image = 0, isVisible = true),
            ),
        )

        assert(result)
    }

    @Test
    fun `check for skucha when three dice are visible and there are no available points`() {
        val result = checkForSkuchaUseCase.invoke(
            diceList = listOf(
                Dice(value = 6, image = 0, isVisible = true),
                Dice(value = 2, image = 0, isVisible = true),
                Dice(value = 3, image = 0, isVisible = true),
                Dice(value = 1, image = 0, isVisible = false),
                Dice(value = 1, image = 0, isVisible = false),
                Dice(value = 1, image = 0, isVisible = false),
            ),
        )

        assert(result)
    }

}