package pl.kazoroo.tavernFarkle.presentation.game

import org.junit.Before
import org.junit.Test
import pl.kazoroo.tavernFarkle.core.data.presentation.BettingActions
import pl.kazoroo.tavernFarkle.game.presentation.game.GameViewModel

class FakeBettingViewModel: BettingActions {
    override fun addBetCoinsToTotalCoinsAmount() {

    }
}

class GameViewModelTest {
    private lateinit var viewModel: GameViewModel

    @Before
    fun initiate() {
        viewModel = GameViewModel(bettingActions = FakeBettingViewModel(), ownedSpecialDices = listOf())
    }

    @Test
    fun toggleDiceSelection() {
        viewModel.toggleDiceSelection(1)
        viewModel.toggleDiceSelection(5)

        assert(viewModel.diceState.value.isDiceSelected == listOf(false, true, false, false, false, true))
    }

    @Test
    fun prepareForNextThrow() {
        viewModel.toggleDiceSelection(1)
        viewModel.toggleDiceSelection(5)

        viewModel.prepareForNextThrow()

        assert(viewModel.diceState.value.isDiceVisible == listOf(true, false, true, true, true, false))

        viewModel.toggleDiceSelection(3)

        viewModel.prepareForNextThrow()

        assert(viewModel.diceState.value.isDiceVisible == listOf(true, false, true, false, true, false))
    }
}