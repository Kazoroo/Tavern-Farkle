package pl.kazoroo.tavernFarkle.multiplayer

import kotlinx.coroutines.delay
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.model.Player
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.createDiceSet
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.padWithNullsToSix
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.signInAnonymouslyOrGetExistingUid
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.FirebaseDataSource
import pl.kazoroo.tavernFarkle.multiplayer.data.repository.RemoteGameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class JoinLobbyUseCase(
    private val gameRepository: RemoteGameRepository,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val firebaseDataSource: FirebaseDataSource
) {
    suspend operator fun invoke(
        userDiceNames: List<SpecialDiceName>,
        gameUuid: String
    ) {
        val currentPlayerId = signInAnonymouslyOrGetExistingUid()
        val paddedUserDiceNames = userDiceNames.padWithNullsToSix()
        val userDiceSet = createDiceSet(paddedUserDiceNames, gameRepository, drawDiceUseCase)
        val player = Player(currentPlayerId, diceSet = userDiceSet)
        firebaseDataSource.addPlayerToLobby(gameUuid, player.toDto())

        val gameState = retryReadGameState(gameUuid)
            ?: throw Exception("Game state is null after 3 attempts")

        gameRepository.saveGameState(gameState)
        gameRepository.setMyUuid(currentPlayerId)
    }

    private suspend fun retryReadGameState(
        gameUuid: String,
    ): GameState? {
        repeat(2) {
            val result = firebaseDataSource.readGameData(gameUuid)?.toDomain()
            if (result != null) return result
            delay(100L)
        }

        return firebaseDataSource.readGameData(gameUuid)?.toDomain()
    }
}