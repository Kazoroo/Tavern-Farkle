package pl.kazoroo.tavernFarkle.multiplayer.presentation

import androidx.lifecycle.ViewModel
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.StartNewGameUseCase
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class LobbyViewModel(
    private val remoteGameRepository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
): ViewModel() {
    fun startNewGame(bet: Int, selectedSpecialDiceNames: List<SpecialDiceName>) {
        StartNewGameUseCase(remoteGameRepository, drawDiceUseCase).invoke(bet, selectedSpecialDiceNames)
    }
}