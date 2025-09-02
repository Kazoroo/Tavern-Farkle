package pl.kazoroo.tavernFarkle.presentation.game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import pl.kazoroo.tavernFarkle.domain.usecase.FakeDrawDiceUseCase
import pl.kazoroo.tavernFarkle.game.data.repository.LocalGameRepository
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.PlayOpponentTurnUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.StartNewGameUseCase
import pl.kazoroo.tavernFarkle.game.presentation.game.GameViewModelRefactor
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelIntegrationTest {
    private lateinit var viewModel: GameViewModelRefactor
    private lateinit var fakeDrawDiceUseCase: FakeDrawDiceUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val repository = LocalGameRepository()
        fakeDrawDiceUseCase = FakeDrawDiceUseCase(repository)
        StartNewGameUseCase(repository, fakeDrawDiceUseCase).invoke(0)
        val calculatePointsUseCase = CalculatePointsUseCase(repository)

        viewModel = GameViewModelRefactor(
            repository = repository,
            calculatePointsUseCase = calculatePointsUseCase,
            drawDiceUseCase = fakeDrawDiceUseCase,
            playOpponentTurnUseCase = PlayOpponentTurnUseCase(
                repository = repository,
                drawDiceUseCase = fakeDrawDiceUseCase,
                calculatePointsUseCase = calculatePointsUseCase
            ),
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleDiceSelection should select dice and calculate points`() {
        viewModel.toggleDiceSelection(0)

        val playerState = viewModel.gameState.value.players[0]

        playerState.diceSet.forEachIndexed { index, dice ->
            if(index == 0) assertTrue(dice.isSelected)
            else assertTrue(!dice.isSelected)
        }
        assertEquals(playerState.selectedPoints, 100)
        assertEquals(playerState.roundPoints, 0)
        assertEquals(playerState.totalPoints, 0)
    }

    @Test
    fun `onScoreAndRollAgain should sum round points, hide selected dice and draw dice`() {
        viewModel.toggleDiceSelection(2)
        viewModel.toggleDiceSelection(3)
        viewModel.onScoreAndRollAgain()

        println(viewModel.gameState.value.players[0].diceSet.joinToString(separator = "\n"))

        assertEquals(viewModel.gameState.value.players[0].roundPoints, 200)
        assertEquals(viewModel.gameState.value.players[0].selectedPoints, 0)
        assertEquals(viewModel.gameState.value.players[0].totalPoints, 0)

        assertFalse(viewModel.gameState.value.players[0].diceSet[2].isSelected)
        assertFalse(viewModel.gameState.value.players[0].diceSet[2].isVisible)
        assertFalse(viewModel.gameState.value.players[0].diceSet[3].isSelected)
        assertFalse(viewModel.gameState.value.players[0].diceSet[3].isVisible)

        viewModel.gameState.value.players[0].diceSet.forEach { assertEquals(it.value, 5) }
    }

    @Test
    fun `onPass should sum total points, reset dice state, change current player and draw dice for second player`() {
        viewModel.toggleDiceSelection(0)
        viewModel.onPass()

        assertEquals(viewModel.gameState.value.players[0].totalPoints, 100)
        assertEquals(viewModel.gameState.value.players[0].roundPoints, 0)
        assertEquals(viewModel.gameState.value.players[0].selectedPoints, 0)

        viewModel.gameState.value.players[0].diceSet.forEach {
            assertFalse(it.isSelected)
            assertTrue(it.isVisible)
        }

        assertEquals(viewModel.gameState.value.currentPlayerUuid, viewModel.gameState.value.players[1].uuid)

        viewModel.gameState.value.players[0].diceSet.forEach { assertEquals(it.value, 1) }
        viewModel.gameState.value.players[1].diceSet.forEach { assertEquals(it.value, 5) }
    }

    @Test
    fun `after user pass the computer play its round`() = runTest {
        viewModel.toggleDiceSelection(0)
        viewModel.onPass()

        advanceUntilIdle()

        assertEquals(viewModel.gameState.value.players[0].roundPoints, 0)
        assertEquals(viewModel.gameState.value.players[0].totalPoints, 100)
        assertEquals(viewModel.gameState.value.players[1].roundPoints, 0)
        assertEquals(viewModel.gameState.value.players[1].totalPoints, 2000)
        assertEquals(viewModel.gameState.value.currentPlayerUuid, viewModel.gameState.value.players[0].uuid)
        viewModel.gameState.value.players[0].diceSet.forEach {
            assertTrue(it.isVisible)
            assertEquals(it.value, 5)
            assertFalse(it.isSelected)
        }
    }
}