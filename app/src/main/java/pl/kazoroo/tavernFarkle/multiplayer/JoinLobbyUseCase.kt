package pl.kazoroo.tavernFarkle.multiplayer

import pl.kazoroo.tavernFarkle.core.domain.model.Player
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.createDiceSet
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.padWithNullsToSix
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.FirebaseDataSource
import pl.kazoroo.tavernFarkle.multiplayer.data.repository.RemoteGameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import java.util.UUID

class JoinLobbyUseCase(
    private val gameRepository: RemoteGameRepository,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val firebaseDataSource: FirebaseDataSource
) {
    suspend operator fun invoke(
        userDiceNames: List<SpecialDiceName>,
        gameUuid: String
    ) {
        val currentPlayerId = UUID.randomUUID()
        val paddedUserDiceNames = userDiceNames.padWithNullsToSix()
        val userDiceSet = createDiceSet(paddedUserDiceNames, gameRepository, drawDiceUseCase)
        val player = Player(currentPlayerId, diceSet = userDiceSet)
        firebaseDataSource.addPlayerToLobby(gameUuid, player)

        val gameState = firebaseDataSource.readGameData(gameUuid)?.toDomain() ?: throw Exception("Game state is null")

        println("game state: $gameState")

        gameRepository.saveGameState(gameState)
        gameRepository.setMyUuid(currentPlayerId)
    }
}