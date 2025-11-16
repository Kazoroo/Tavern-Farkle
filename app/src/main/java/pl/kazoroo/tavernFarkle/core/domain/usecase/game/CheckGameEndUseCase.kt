package pl.kazoroo.tavernFarkle.core.domain.usecase.game

import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository

class CheckGameEndUseCase() {
    operator fun invoke(
        repository: GameRepository,
        sumCoins: () -> Unit = {},
    ): Boolean {
        if(repository.gameState.value.players[repository.gameState.value.getCurrentPlayerIndex()].totalPoints >= 400) {
            repository.setGameEnd(true)

            return true
        }

        return false
    }
}