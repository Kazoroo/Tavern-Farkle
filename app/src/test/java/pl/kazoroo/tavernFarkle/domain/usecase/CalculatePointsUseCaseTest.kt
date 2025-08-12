package pl.kazoroo.tavernFarkle.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.game.data.repository.LocalGameRepository
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.model.Player
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
import java.util.UUID
import kotlin.random.Random

open class CalculatePointsUseCaseTest {
    lateinit var calculatePointsUseCase: CalculatePointsUseCase

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

        calculatePointsUseCase = CalculatePointsUseCase(repository)
    }

    @Test
    fun `check if three 1s gives 1000 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 2, image = 0, isSelected = false),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 4, image = 0, isSelected = false),
            )
        )

        assertEquals(1000, result)
    }

    @Test
    fun `check if game count not selected dices`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isSelected = false),
                Dice(value = 1, image = 0, isSelected = false),
                Dice(value = 1, image = 0, isSelected = false),
                Dice(value = 2, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = true),
            )
        )

        assertEquals(0, result)
    }

    @Test
    fun `check if four 1s gives 2000 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 4, image = 0, isSelected = false),
            ),
        )

        assertEquals(2000, result)
    }

    @Test
    fun `check if four 5s gives 1000 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 4, image = 0, isSelected = false),
            ),
        )

        assertEquals(1000, result)
    }

    @Test
    fun `check if six 5s gives 2000 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
            ),
        )

        assertEquals(2000, result)
    }

    @Test
    fun `check if four 3s gives 600 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = false),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = false),
            ),
        )

        assertEquals(600, result)
    }
    @Test
    fun `check if five 3s gives 900 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = false),
            ),
        )

        assertEquals(900, result)
    }

    @Test
    fun `check if one 5 gives 50 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 1, image = 0, isSelected = false),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 4, image = 0, isSelected = false),
            ),
        )

        assertEquals(50, result)
    }

    @Test
    fun `check if two 5 gives 100 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 1, image = 0, isSelected = false),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 4, image = 0, isSelected = false),
            ),
        )

        assertEquals(100, result)
    }

    @Test
    fun `check if straight 1 to 5 gives 500 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 2, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = false),
            ),
        )

        assertEquals(500, result)
    }

    @Test
    fun `check if straight 1 to 5 plus 1 gives 600 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 2, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = true),
            ),
        )

        assertEquals(600, result)
    }

    @Test
    fun `check if straight 1 to 5 plus 5 gives 550 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 2, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = true),
            ),
        )

        assertEquals(550, result)
    }

    @Test
    fun `check if straight 2 to 6 gives 750 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isSelected = false),
                Dice(value = 6, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = true),
                Dice(value = 2, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
            ),
        )

        assertEquals(750, result)
    }

    @Test
    fun `check if straight 1 to 6 gives 1500 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 2, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 6, image = 0, isSelected = true),
            ),
        )

        assertEquals(1500, result)
    }

    @Test
    fun `check if straight 1 to 5 plus mismatched dice gives 0 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 1, image = 0, isSelected = true),
                Dice(value = 2, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 4, image = 0, isSelected = true),
            ),
        )

        assertEquals(0, result)
    }

    @Test
    fun `check if two 5 and mismatched dice gives 0 points`() {
        val result = calculatePointsUseCase.invoke(
            diceList = listOf(
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 5, image = 0, isSelected = true),
                Dice(value = 3, image = 0, isSelected = true),
                Dice(value = 1, image = 0, isSelected = false),
                Dice(value = 3, image = 0, isSelected = false),
                Dice(value = 4, image = 0, isSelected = false),
            ),
        )

        assertEquals(0, result)
    }
}