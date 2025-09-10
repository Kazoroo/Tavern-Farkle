package pl.kazoroo.tavernFarkle.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pl.kazoroo.tavernFarkle.game.data.repository.LocalGameRepository
import pl.kazoroo.tavernFarkle.game.domain.model.Dice
import pl.kazoroo.tavernFarkle.game.domain.model.GameState
import pl.kazoroo.tavernFarkle.game.domain.model.Player
import java.util.UUID
import kotlin.test.assertNotEquals

class LocalGameRepositoryTest {
    lateinit var repository: LocalGameRepository

    val diceSetFirstPlayer = List(6) { index ->
        Dice(
            value = index + 1,
            image = index
        )
    }
    val diceSetSecondPlayer = List(6) { index ->
        Dice(
            value = index + 7,
            image = index
        )
    }

    val players = listOf(
        Player(
            uuid = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            totalPoints = 900,
            roundPoints = 200,
            selectedPoints = 50,
            diceSet = diceSetFirstPlayer
        ),
        Player(
            uuid = UUID.fromString("00000000-2222-0000-0000-000000000000"),
            totalPoints = 1200,
            roundPoints = 0,
            selectedPoints = 0,
            diceSet = diceSetSecondPlayer
        )
    )

    val gameState = GameState(
        betAmount = 50,
        gameUuid = UUID.fromString("00000000-0000-0000-0000-000000000000"),
        isSkucha = true,
        currentPlayerUuid = UUID.fromString("11111111-1111-1111-1111-111111111111"),
        players = players
    )

    @Before
    fun init() {
        repository = LocalGameRepository()

        repository.saveGameState(gameState)
    }

    @Test
    fun `gameState has initial value`() {
        assertEquals(gameState, repository.gameState.value)
    }

    @Test
    fun `getCurrentPlayerIndex return actual player index`() {
        repository.saveGameState(
            GameState(
                players = players,
                currentPlayerUuid = players[1].uuid,
                gameUuid = UUID.randomUUID(),
                betAmount = 80
            )
        )

        assertEquals(1,  repository.currentPlayerIndex)
        assertEquals(players[1],  repository.currentPlayer)
    }

    @Test
    fun `toggleDiceSelection should toggle dice selection state not affecting other values`() {
        val diceValue = repository.gameState.value.players[0].diceSet[0].value
        val isDiceVisible = repository.gameState.value.players[0].diceSet[0].isVisible

        repository.toggleDiceSelection(0)
        assertTrue(repository.currentPlayer.diceSet[0].isSelected)
        assertEquals(diceValue, repository.currentPlayer.diceSet[0].value)
        assertEquals(isDiceVisible, repository.currentPlayer.diceSet[0].isVisible)

        repository.toggleDiceSelection(0)
        assertFalse(repository.currentPlayer.diceSet[0].isSelected)
        assertEquals(diceValue, repository.currentPlayer.diceSet[0].value)
        assertEquals(isDiceVisible, repository.currentPlayer.diceSet[0].isVisible)
    }

    @Test
    fun `updateSelectedPoints should update current player selected points`() {
        repository.updateSelectedPoints(300)
        assertEquals(300, repository.currentPlayer.selectedPoints)
    }

    @Test
    fun `sumRoundPoints should add selected points to round points and reset selected only for current player`() {
        repository.sumRoundPoints()

        val updatedPlayer = repository.gameState.value.players[0]
        assertEquals(900, updatedPlayer.totalPoints)
        assertEquals(250, updatedPlayer.roundPoints)
        assertEquals(0, updatedPlayer.selectedPoints)

        val secondPlayer = repository.gameState.value.players[1]
        assertEquals(1200, secondPlayer.totalPoints)
        assertEquals(0, secondPlayer.roundPoints)
        assertEquals(0, secondPlayer.selectedPoints)
    }

    @Test
    fun `hideSelectedDice should hide and deselect selected dice`() {
        repository.toggleDiceSelection(0)
        repository.toggleDiceSelection(4)
        repository.hideSelectedDice()

        repository.currentPlayer.diceSet.forEachIndexed { index, dice ->
            if (index == 0 || index == 4) {
                assertFalse(dice.isVisible)
                assertFalse(dice.isSelected)
            } else {
                assertTrue(dice.isVisible)
                assertFalse(dice.isSelected)
            }
        }
    }

    @Test
    fun `hideSelectedDice when no selected dice should not change anything`() {
        repository.hideSelectedDice()

        repository.currentPlayer.diceSet.forEachIndexed { index, dice ->
            assertTrue(dice.isVisible)
            assertFalse(dice.isSelected)
        }
    }

    @Test
    fun `updateDiceSet correctly changes diceSet only for current player`() {
        val newDiceSet = List(6) { index ->
            Dice(value = 77, image = index)
        }

        repository.updateDiceSet(newDiceSet)

        assertEquals(newDiceSet, repository.currentPlayer.diceSet)
        assertNotEquals(newDiceSet, repository.gameState.value.players[1].diceSet)
    }
}