package pl.kazoroo.tavernFarkle.multiplayer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.StartNewGameUseCaseFactory
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType
import pl.kazoroo.tavernFarkle.multiplayer.JoinLobbyUseCase
import pl.kazoroo.tavernFarkle.multiplayer.data.model.Lobby
import pl.kazoroo.tavernFarkle.multiplayer.data.repository.RemoteGameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class LobbyViewModel(
    private val remoteGameRepository: RemoteGameRepository,
    private val startNewGameUseCaseFactory: StartNewGameUseCaseFactory,
    private val joinLobbyUseCase: JoinLobbyUseCase
): ViewModel() {
    val lobbyList: StateFlow<List<Lobby>> = remoteGameRepository.lobbyList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L, 0), emptyList())

    init {
        remoteGameRepository.observeLobbyList()
    }

    fun startNewGame(
        bet: Int,
        selectedSpecialDiceNames: List<SpecialDiceName>,
        onNavigate: () -> Unit,
        setBetValue: (bet: String) -> Unit
    ) {
        startNewGameUseCaseFactory.create(isMultiplayer = true)
            .invoke(
                bet,
                selectedSpecialDiceNames,
                isMultiplayer = true
            )

        remoteGameRepository.saveGameDataToDatabase(gameState = remoteGameRepository.gameState.value)
        remoteGameRepository.observeGameData(gameState = remoteGameRepository.gameState.value)

        SoundPlayer.playSound(SoundType.CLICK)
        onNavigate()
        setBetValue(bet.toString())
    }

    fun joinLobby(
        gameUuid: String,
        selectedSpecialDiceNames: List<SpecialDiceName>,
        bet: Int,
        setBetValue: (bet: String) -> Unit,
        onNavigate: () -> Unit
    ) {
        viewModelScope.launch {
            joinLobbyUseCase.invoke(
                selectedSpecialDiceNames,
                gameUuid
            )

            remoteGameRepository.observeGameData(gameState = remoteGameRepository.gameState.value)
        }

        SoundPlayer.playSound(SoundType.CLICK)
        onNavigate()
        setBetValue(bet.toString())
    }
}