package pl.kazoroo.tavernFarkle.multiplayer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.StartNewGameUseCase
import pl.kazoroo.tavernFarkle.multiplayer.data.model.Lobby
import pl.kazoroo.tavernFarkle.multiplayer.data.repository.RemoteGameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class LobbyViewModel(
    private val remoteGameRepository: RemoteGameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
): ViewModel() {
    val lobbyList: StateFlow<List<Lobby>> = remoteGameRepository.lobbyList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L, 0), emptyList())

    init {
        remoteGameRepository.observeLobbyList()
    }

    fun startNewGame(bet: Int, selectedSpecialDiceNames: List<SpecialDiceName>) {
        StartNewGameUseCase(remoteGameRepository, drawDiceUseCase).invoke(bet, selectedSpecialDiceNames)
    }
}