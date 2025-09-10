package pl.kazoroo.tavernFarkle.presentation.game

import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import pl.kazoroo.tavernFarkle.domain.usecase.FakeDrawDiceUseCase
import pl.kazoroo.tavernFarkle.game.data.repository.LocalGameRepository
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.StartNewGameUseCase
import pl.kazoroo.tavernFarkle.game.presentation.game.GameViewModelRefactor
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameViewModelIntegrationTest {
    private lateinit var viewModel: GameViewModelRefactor
    private lateinit var fakeDrawDiceUseCase: FakeDrawDiceUseCase

    @Before
    fun setUp() {
        val repository = LocalGameRepository()
        println(repository.gameState.value.players.joinToString(separator = "\n"))
        fakeDrawDiceUseCase = FakeDrawDiceUseCase(repository)
        StartNewGameUseCase(repository, fakeDrawDiceUseCase).invoke(0)

        viewModel = GameViewModelRefactor(
            repository = repository,
            calculatePointsUseCase = CalculatePointsUseCase(repository),
            drawDiceUseCase = fakeDrawDiceUseCase
        )
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

        assertEquals(viewModel.gameState.value.players[0].roundPoints, 200)
        assertEquals(viewModel.gameState.value.players[0].selectedPoints, 0)
        assertEquals(viewModel.gameState.value.players[0].totalPoints, 0)

        assertFalse(viewModel.gameState.value.players[0].diceSet[2].isSelected)
        assertFalse(viewModel.gameState.value.players[0].diceSet[2].isVisible)
        assertFalse(viewModel.gameState.value.players[0].diceSet[3].isSelected)
        assertFalse(viewModel.gameState.value.players[0].diceSet[3].isVisible)

        assertEquals(viewModel.gameState.value.players[0].diceSet, fakeDrawDiceUseCase.diceSet2)
    }
}