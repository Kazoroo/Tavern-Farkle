package pl.kazoroo.tavernFarkle.core.domain.usecase.game

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.model.Player
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import java.util.UUID
import kotlin.coroutines.resume

class StartNewGameUseCase(
    private val gameRepository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
) {
    suspend operator fun invoke(
        betAmount: Int,
        userDiceNames: List<SpecialDiceName>,
        isMultiplayer: Boolean,
        targetScore: Int = GameState.DEFAULT_TARGET_SCORE
    ) {
        val paddedUserDiceNames = userDiceNames.padWithNullsToSix()
        val userDiceSet = createDiceSet(
            specialDiceNames = paddedUserDiceNames,
            gameRepository = gameRepository,
            drawDiceUseCase = drawDiceUseCase,
            isMultiplayer = isMultiplayer,
            checkForSkucha = !isMultiplayer
        )

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
                Player(
                    uuid = UUID.randomUUID().toString(),
                    diceSet = createDiceSet(
                        opponentDiceNames,
                        gameRepository,
                        drawDiceUseCase,
                        isMultiplayer = false
                    )
                )
            )
        }

        val currentSkuchaStatus = gameRepository.gameState.value.isSkucha
        val gameState = GameState(
            betAmount = betAmount,
            gameUuid = UUID.randomUUID(),
            isAnimating = false,
            isSkucha = currentSkuchaStatus,
            currentPlayerUuid = players.first().uuid,
            players = players,
            isGameEnd = false,
            targetScore = targetScore,
        )

        gameRepository.saveGameState(gameState)
        gameRepository.setMyUuid(players.first().uuid)
    }
}

suspend fun signInAnonymouslyOrGetExistingUid(): String = suspendCancellableCoroutine { cont ->
    val auth = FirebaseAuth.getInstance()

    auth.currentUser?.uid?.let {
        cont.resume(it)
        return@suspendCancellableCoroutine
    }

    auth.signInAnonymously()
        .addOnSuccessListener { result ->
            cont.resume(result.user?.uid ?: UUID.randomUUID().toString())
        }
        .addOnFailureListener {
            cont.resume(UUID.randomUUID().toString())
        }
}

fun createDiceSet(
    specialDiceNames: List<SpecialDiceName?>,
    gameRepository: GameRepository,
    drawDiceUseCase: DrawDiceUseCase,
    isMultiplayer: Boolean,
    checkForSkucha: Boolean = true
) =
    drawDiceUseCase(
        List(6) { index ->
            Dice(value = 0, image = 0, specialDiceName = specialDiceNames[index])
        },
        repository = gameRepository,
        checkForSkucha = checkForSkucha,
        isMultiplayer = isMultiplayer
    )

fun List<SpecialDiceName?>.padWithNullsToSix(): List<SpecialDiceName?> =
    take(6) + List((6 - size).coerceAtLeast(0)) { null }
