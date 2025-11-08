package pl.kazoroo.tavernFarkle.core.domain.usecase.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType

class CheckGameEndUseCase() {
    operator fun invoke(
        repository: GameRepository,
        sumCoins: () -> Unit = {},
    ): Boolean {
        if(repository.gameState.value.players[repository.gameState.value.getCurrentPlayerIndex()].totalPoints >= 4000) {
            CoroutineScope(Dispatchers.Main).launch {
                if (repository.gameState.value.currentPlayerUuid == repository.myUuidState.value) {
                    SoundPlayer.playSound(SoundType.WIN)
                    sumCoins()
                } else {
                    SoundPlayer.playSound(SoundType.FAILURE)
                }
                repository.setGameEnd(true)
            }

            return true
        }

        return false
    }
}