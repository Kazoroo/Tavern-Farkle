package pl.kazoroo.tavernFarkle.game.domain.usecase

import kotlinx.coroutines.delay
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository

class PlayOpponentTurnUseCase(
    private val repository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val calculatePointsUseCase: CalculatePointsUseCase
) {
    suspend operator fun invoke() {
        val playingUntilDiceLeft = (2..3).random()
        var numberOfVisibleDice = 6

        while(numberOfVisibleDice > playingUntilDiceLeft) {
            delay((1600L..2000L).random())
            val indexesOfDiceGivingPoints = searchForDiceIndexGivingPoints()

            if (indexesOfDiceGivingPoints.isEmpty()) {
                //TODO: perform skucha

                return
            }

            indexesOfDiceGivingPoints.forEach {
                repository.toggleDiceSelection(it)
                calculatePointsUseCase(repository.gameState.value.players[1].diceSet)
                delay((1200L..1600L).random())
            }

            if (numberOfVisibleDice - indexesOfDiceGivingPoints.size > playingUntilDiceLeft) {
                scoreAndRollAgain()
                numberOfVisibleDice = repository.gameState.value.players[1].diceSet.count { it.isVisible }
            } else {
                passRound()
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

    private fun passRound() {
        repository.sumTotalPoints()
        repository.resetDiceState()
        repository.changeCurrentPlayer()

        val state = repository.gameState.value
        val currentPlayer = state.players[state.getCurrentPlayerIndex()]

        drawDiceUseCase(currentPlayer.diceSet)
    }

    private fun scoreAndRollAgain() {
        repository.sumRoundPoints()
        repository.hideSelectedDice()

        val state = repository.gameState.value
        val currentPlayer = state.players[state.getCurrentPlayerIndex()]

        drawDiceUseCase(currentPlayer.diceSet)
    }
}