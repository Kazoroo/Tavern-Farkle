package pl.kazoroo.tavernFarkle.multiplayer.presentation

import androidx.lifecycle.ViewModel
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.game.domain.usecase.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.game.domain.usecase.StartNewGameUseCase
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class LobbyViewModel(
    private val remoteGameRepository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
): ViewModel() {
    fun startNewGame(bet: Int, selectedSpecialDiceNames: List<SpecialDiceName>) {
        StartNewGameUseCase(remoteGameRepository, drawDiceUseCase).invoke(bet, selectedSpecialDiceNames)
    }
}