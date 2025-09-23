package pl.kazoroo.tavernFarkle.game.domain.usecase

import kotlinx.coroutines.delay
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository

class PlayOpponentTurnUseCase(
    private val repository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val calculatePointsUseCase: CalculatePointsUseCase,
    private val checkGameEndUseCase: CheckGameEndUseCase
) {
    suspend operator fun invoke(triggerDiceRowAnimation: suspend () -> Unit) {
        val playingUntilDiceLeft = (2..3).random()
        var numberOfVisibleDice = 6

        while(numberOfVisibleDice > playingUntilDiceLeft) {
            delay((1400L..1400L).random())
            val indexesOfDiceGivingPoints = searchForDiceIndexGivingPoints()

            if (indexesOfDiceGivingPoints.isEmpty()) {
                return
            }

            indexesOfDiceGivingPoints.forEach {
                repository.toggleDiceSelection(it)
                calculatePointsUseCase(
                    repository.gameState.value.players[1].diceSet,
                    repository = repository
                )
                delay((1200L..1600L).random())
            }

            if (numberOfVisibleDice - indexesOfDiceGivingPoints.size > playingUntilDiceLeft || numberOfVisibleDice - indexesOfDiceGivingPoints.size == 0) {
                scoreAndRollAgain(triggerDiceRowAnimation)
                numberOfVisibleDice = repository.gameState.value.players[1].diceSet.count { it.isVisible }
            } else {
                passRound(
                    triggerDiceRowAnimation,
                    repository
                )
                break
            }
        }
    }

    /**
     * @return List of dice indexes that gives points
     */
    private fun searchForDiceIndexGivingPoints(): List<Int> {
        val gameState = repository.gameState.value
        val opponentDiceSet = gameState.players[1].diceSet
        val sequenceDice: List<Int> =
            opponentDiceSet.filterIndexed { index, _ -> opponentDiceSet[index].isVisible }
                .groupingBy { it.value }.eachCount().filter { it.value >= 3 }.keys.toList()

        val indexesOfDiceGivingPoints = opponentDiceSet.mapIndexedNotNull { index, dice ->
            if ((dice.value == 1 || dice.value == 5 || sequenceDice.contains(dice.value)) && opponentDiceSet[index].isVisible) index else null
        }.shuffled()

        return indexesOfDiceGivingPoints
    }

    private suspend fun passRound(
        triggerDiceRowAnimation: suspend () -> Unit,
        repository: GameRepository
    ) {
        repository.sumTotalPoints()

        if(checkGameEndUseCase(repository = repository)) return

        triggerDiceRowAnimation()
        repository.resetDiceState()
        repository.changeCurrentPlayer()

        val state = repository.gameState.value
        val currentPlayer = state.players[state.getCurrentPlayerIndex()]

        drawDiceUseCase(
            currentPlayer.diceSet,
            repository = repository
        )
    }

    private suspend fun scoreAndRollAgain(triggerDiceRowAnimation: suspend () -> Unit) {
        repository.sumRoundPoints()
        repository.hideSelectedDice()
        triggerDiceRowAnimation()

        if(repository.gameState.value.players[repository.gameState.value.getCurrentPlayerIndex()].diceSet.all { !it.isVisible }) {
            repository.resetDiceState()
        }

        val state = repository.gameState.value
        val currentPlayer = state.players[state.getCurrentPlayerIndex()]

        drawDiceUseCase(
            currentPlayer.diceSet,
            repository = repository
        )
    }
}