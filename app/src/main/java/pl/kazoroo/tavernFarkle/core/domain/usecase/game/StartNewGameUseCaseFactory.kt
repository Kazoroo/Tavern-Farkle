package pl.kazoroo.tavernFarkle.core.domain.usecase.game

import pl.kazoroo.tavernFarkle.multiplayer.data.repository.RemoteGameRepository
import pl.kazoroo.tavernFarkle.singleplayer.data.repository.LocalGameRepository

class StartNewGameUseCaseFactory(
    private val localRepo: LocalGameRepository,
    private val remoteRepo: RemoteGameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
) {
    fun create(isMultiplayer: Boolean): StartNewGameUseCase {
        val repo = if (isMultiplayer) remoteRepo else localRepo
        return StartNewGameUseCase(repo, drawDiceUseCase)
    }
}
