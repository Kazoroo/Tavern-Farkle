package pl.kazoroo.tavernFarkle.core.domain.usecase.game

import com.google.firebase.auth.FirebaseAuth
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.model.Player
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class StartNewGameUseCase(
    private val gameRepository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
) {
    suspend operator fun invoke(
        betAmount: Int,
        userDiceNames: List<SpecialDiceName>,
        isMultiplayer: Boolean
    ) {
        val paddedUserDiceNames = userDiceNames.padWithNullsToSix()
        val userDiceSet = createDiceSet(paddedUserDiceNames, gameRepository, drawDiceUseCase)

        val players = if(isMultiplayer) {
            listOf(Player(
                uuid = signInAnonymouslyOrGetExistingUid(),
                diceSet = userDiceSet
            ))
        } else {
            val opponentDiceNames: List<SpecialDiceName?> = List(
                (userDiceNames.size..userDiceNames.size + 1).random()
            ) {
                SpecialDiceName.entries.toTypedArray().random()
            }.padWithNullsToSix()

            listOf(
                Player(uuid = UUID.randomUUID().toString(), diceSet = userDiceSet),
                Player(uuid = UUID.randomUUID().toString(), diceSet = createDiceSet(opponentDiceNames, gameRepository, drawDiceUseCase))
            )
        }

        val currentSkuchaStatus = gameRepository.gameState.value.isSkucha
        val gameState = GameState(
            betAmount = betAmount,
            gameUuid = UUID.randomUUID(),
            isSkucha = currentSkuchaStatus,
            currentPlayerUuid = players.first().uuid,
            players = players,
            isGameEnd = false,
        )

        gameRepository.saveGameState(gameState)
        gameRepository.setMyUuid(players.first().uuid)
    }
}

suspend fun signInAnonymouslyOrGetExistingUid(): String = suspendCoroutine { cont ->
    val auth = FirebaseAuth.getInstance()

    auth.currentUser?.uid?.let {
        cont.resume(it)
        return@suspendCoroutine
    }

    auth.signInAnonymously()
        .addOnSuccessListener { result ->
            cont.resume(result.user?.uid ?: error("No UID"))
        }
        .addOnFailureListener { exception ->
            cont.resumeWithException(exception)
        }
}


fun createDiceSet(specialDiceNames: List<SpecialDiceName?>, gameRepository: GameRepository, drawDiceUseCase: DrawDiceUseCase) =
    drawDiceUseCase(
        List(6) { index ->
            Dice(value = 0, image = 0, specialDiceName = specialDiceNames[index])
        },
        repository = gameRepository
    )


fun List<SpecialDiceName?>.padWithNullsToSix(): List<SpecialDiceName?> =
    take(6) + List((6 - size).coerceAtLeast(0)) { null }